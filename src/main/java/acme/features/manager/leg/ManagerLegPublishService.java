
package acme.features.manager.leg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.entities.leg.LegRepository;
import acme.entities.leg.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegPublishService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private LegRepository			legRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.getLegById(legId);
		Flight flight = leg.getFlight();
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		boolean status = true;
		boolean isLegDraft = leg.getIsDraftMode();
		boolean isManager = super.getRequest().getPrincipal().hasRealm(manager);
		boolean isOwner = super.getRequest().getPrincipal().getAccountId() == flight.getManager().getUserAccount().getId();
		status = isLegDraft && isManager && isOwner;
		if (super.getRequest().getMethod().equals("POST"))
			status = status && this.validateAircraft() && this.validateAirport("departureAirport") && this.validateAirport("arrivalAirport");

		super.getResponse().setAuthorised(status);
	}

	private boolean validateAircraft() {
		Integer aircraftId = super.getRequest().getData("aircraft", int.class);
		if (aircraftId != 0) {
			Aircraft aircraft = this.repository.findAircraftById(aircraftId);
			if (aircraft == null)
				return false;
		}
		return true;
	}

	private boolean validateAirport(final String airportField) {
		Integer airportId = super.getRequest().getData(airportField, int.class);
		if (airportId != 0) {
			Airport airport = this.repository.findAirportById(airportId);
			if (airport == null)
				return false;
		}
		return true;
	}

	@Override
	public void load() {
		Leg leg;
		int legId;

		legId = super.getRequest().getData("id", int.class);
		leg = this.repository.getLegById(legId);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		int departureAirportId;
		int arrivalAirportId;
		int aircraftId;
		Airport departureAirport;
		Airport arrivalAirport;
		Aircraft aircraft;

		departureAirportId = super.getRequest().getData("departureAirport", int.class);
		arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		aircraftId = super.getRequest().getData("aircraft", int.class);
		departureAirport = this.repository.findAirportById(departureAirportId);
		arrivalAirport = this.repository.findAirportById(arrivalAirportId);
		aircraft = this.repository.findAircraftById(aircraftId);

		super.bindObject(leg, "flightNumber", "departure", "arrival", "status");

		leg.setDepartureAirport(departureAirport);
		leg.setArrivalAirport(arrivalAirport);
		leg.setAircraft(aircraft);
	}

	@Override
	public void validate(final Leg leg) {
		this.validateScheduledDeparture(leg);
		this.validateOverlappingLegs(leg);
		this.validateAirportSequence(leg);
		this.validateAircraftAvailabilityIfDraft(leg);
	}

	private void validateScheduledDeparture(final Leg leg) {
		Date scheduledDeparture = leg.getDeparture();
		Date currentMoment = MomentHelper.getCurrentMoment();

		boolean validScheduledDeparture = MomentHelper.isAfter(scheduledDeparture, currentMoment);

		super.state(validScheduledDeparture, "scheduledDeparture", "acme.validation.leg.invalid-departure.message");
	}

	private void validateOverlappingLegs(final Leg leg) {
		Collection<Leg> legs = this.legRepository.getLegsByFlight(leg.getFlight().getId());
		List<Leg> legsToValidate = legs.stream().filter(l -> !l.getIsDraftMode()).collect(Collectors.toList());

		List<Leg> legsToValidateOverlap = new ArrayList<>(legsToValidate);
		if (!legsToValidateOverlap.contains(leg))
			legsToValidateOverlap.add(leg);

		List<Leg> sortedLegsOverlap = ManagerLegPublishService.sortLegsByDeparture(legsToValidateOverlap);

		boolean nonOverlappingLegs = true;
		for (int i = 0; i < sortedLegsOverlap.size() - 1; i++) {
			Leg previousLeg = sortedLegsOverlap.get(i);
			Leg nextLeg = sortedLegsOverlap.get(i + 1);

			if (!MomentHelper.isBefore(previousLeg.getArrival(), nextLeg.getDeparture()))
				nonOverlappingLegs = false;
		}

		super.state(nonOverlappingLegs, "*", "acme.validation.flight.overlapping.message");
	}

	private void validateAirportSequence(final Leg leg) {
		Collection<Leg> legs = this.legRepository.getLegsByFlight(leg.getFlight().getId());
		List<Leg> legsToValidate = legs.stream().filter(l -> !l.getIsDraftMode()).collect(Collectors.toList());
		List<Leg> sortedLegs = ManagerLegPublishService.sortLegsByDeparture(legsToValidate);

		Leg previousLeg = null;
		for (Leg candidate : sortedLegs)
			if (candidate.getDeparture().before(leg.getDeparture()))
				if (previousLeg == null || candidate.getDeparture().after(previousLeg.getDeparture()))
					previousLeg = candidate;

		boolean validAirports = true;
		if (!previousLeg.getArrivalAirport().getIATACode().equals(leg.getDepartureAirport().getIATACode()))
			validAirports = false;

		super.state(validAirports, "*", "acme.validation.leg.invalid-airports.message");
	}

	private void validateAircraftAvailabilityIfDraft(final Leg leg) {
		boolean validAircraft = true;
		Aircraft aircraft = leg.getAircraft();

		if (aircraft != null) {
			Date departure = leg.getDeparture();
			Date arrival = leg.getArrival();

			for (Leg l : this.repository.findAllLegs())
				if (!l.equals(leg) && aircraft.equals(l.getAircraft())) {
					Date otherDeparture = l.getDeparture();
					Date otherArrival = l.getArrival();

					boolean overlap = MomentHelper.isBeforeOrEqual(departure, otherArrival) && MomentHelper.isBeforeOrEqual(otherDeparture, arrival);

					if (overlap) {
						validAircraft = false;
						break;
					}
				}
		}

		super.state(validAircraft, "*", "acme.validation.leg.invalid-aircraft.message");
	}

	public static List<Leg> sortLegsByDeparture(final List<Leg> legs) {
		List<Leg> sortedLegs = new ArrayList<>(legs);
		sortedLegs.sort(Comparator.comparing(Leg::getDeparture));
		return sortedLegs;
	}

	@Override
	public void perform(final Leg leg) {
		leg.setIsDraftMode(false);
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		SelectChoices choices;
		choices = SelectChoices.from(LegStatus.class, leg.getStatus());

		SelectChoices departureAirportChoices;
		SelectChoices arrivalAirportChoices;
		Collection<Airport> airports;
		airports = this.repository.findAllAirports();

		SelectChoices selectedAircraft = new SelectChoices();
		Collection<Aircraft> aircraftsActives = this.repository.findAllActiveAircrafts(AircraftStatus.ACTIVE);

		List<Aircraft> finalAircrafts = aircraftsActives.stream().filter(a -> a.getAirline().getIATACode().equals(leg.getFlight().getManager().getAirline().getIATACode())).toList();

		dataset = super.unbindObject(leg, "flightNumber", "departure", "arrival");
		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("isDraftMode", leg.getIsDraftMode());
		dataset.put("status", choices);
		selectedAircraft = SelectChoices.from(finalAircrafts, "regitrationNumber", leg.getAircraft());
		dataset.put("aircrafts", selectedAircraft);
		dataset.put("aircraft", selectedAircraft.getSelected().getKey());
		dataset.put("duration", leg.getDuration());
		dataset.put("isDraftFlight", leg.getFlight().getIsDraftMode());
		dataset.put("IATACode", leg.getFlight().getManager().getAirline().getIATACode());
		departureAirportChoices = SelectChoices.from(airports, "IATACode", leg.getDepartureAirport());
		arrivalAirportChoices = SelectChoices.from(airports, "IATACode", leg.getArrivalAirport());
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalAirportChoices);
		dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

}
