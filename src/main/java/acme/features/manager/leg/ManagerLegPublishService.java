
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

		boolean status = false;

		if (leg != null && leg.getIsDraftMode()) {
			Flight flight = leg.getFlight();
			status = flight != null && flight.getIsDraftMode() && super.getRequest().getPrincipal().hasRealm(flight.getManager()) && super.getRequest().getPrincipal().getAccountId() == flight.getManager().getUserAccount().getId();
		}

		// Validar solo si aircraftId tiene un valor distinto de 0
		Integer aircraftId = super.getRequest().getData("aircraft", int.class);
		if (aircraftId != null && aircraftId != 0) {
			Aircraft aircraft = this.repository.findAircraftById(aircraftId);
			if (aircraft == null || aircraft.getAircraftStatus() != AircraftStatus.ACTIVE)
				status = false;
		}

		// Validar solo si departureAirportId tiene un valor distinto de 0
		Integer departureAirportId = super.getRequest().getData("departureAirport", int.class);
		if (departureAirportId != null && departureAirportId != 0) {
			Airport departureAirport = this.repository.findAirportById(departureAirportId);
			if (departureAirport == null)
				status = false;
		}

		// Validar solo si arrivalAirportId tiene un valor distinto de 0
		Integer arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		if (arrivalAirportId != null && arrivalAirportId != 0) {
			Airport arrivalAirport = this.repository.findAirportById(arrivalAirportId);
			if (arrivalAirport == null)
				status = false;
		}

		String legStatus = super.getRequest().getData("status", String.class);
		if (legStatus != null && !legStatus.equals("0"))
			try {
				LegStatus.valueOf(legStatus);
			} catch (IllegalArgumentException e) {
				status = false;
			}

		super.getResponse().setAuthorised(status);
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

		boolean validScheduledDeparture = true;
		Date scheduledDeparture = leg.getDeparture();
		Date currentMoment = MomentHelper.getCurrentMoment();
		if (scheduledDeparture != null)
			validScheduledDeparture = MomentHelper.isAfter(scheduledDeparture, currentMoment);
		super.state(validScheduledDeparture, "departure", "acme.validation.leg.invalid-departure.message");

		boolean nonOverlappingLegs = true;

		// Tomamos tramos publicados del vuelo
		Collection<Leg> legs = this.legRepository.getLegsByFlight(leg.getFlight().getId());
		List<Leg> legsToValidate = legs.stream().filter(l -> !l.getIsDraftMode()).collect(Collectors.toList());

		// Añadimos el tramo actual a la lista 
		if (!legsToValidate.contains(leg))
			legsToValidate.add(leg);

		// Ordenamos por salida
		List<Leg> sortedLegs = ManagerLegPublishService.sortLegsByDeparture(legsToValidate);

		for (int i = 0; i < sortedLegs.size() - 1; i++) {
			Leg previousLeg = sortedLegs.stream().toList().get(i);
			Leg nextLeg = sortedLegs.stream().toList().get(i + 1);

			if (previousLeg.getArrival() != null && nextLeg.getDeparture() != null) {
				boolean validLeg = MomentHelper.isBefore(previousLeg.getArrival(), nextLeg.getDeparture());
				if (!validLeg)
					nonOverlappingLegs = false;
			}
		}
		super.state(nonOverlappingLegs, "*", "acme.validation.flight.overlapping.message");

		boolean validAirports = true;
		boolean validDate = true;

		for (int i = 0; i < sortedLegs.size() - 1; i++) {
			Leg previousLeg = sortedLegs.get(i);
			Leg nextLeg = sortedLegs.get(i + 1);

			if (previousLeg.getArrivalAirport() != null && nextLeg.getDepartureAirport() != null)
				if (!previousLeg.getArrivalAirport().getIATACode().equals(nextLeg.getDepartureAirport().getIATACode()))
					validAirports = false;

			if (previousLeg.getArrival() != null && nextLeg.getDeparture() != null) {
				long hoursBetween = MomentHelper.computeDuration(previousLeg.getArrival(), nextLeg.getDeparture()).toHours();
				if (hoursBetween >= 48)
					validDate = false;
			}
		}

		super.state(validAirports, "*", "acme.validation.leg.invalid-airports.message");
		super.state(validDate, "*", "acme.validation.leg.invalid-dates.message");

		if (leg.getIsDraftMode()) {
			boolean validAircraft = true;
			Aircraft aircraft = leg.getAircraft();

			if (aircraft != null) {
				Date departure = leg.getDeparture();
				Date arrival = leg.getArrival();

				for (Leg l : this.repository.findAllLegs())
					if (!l.equals(leg) && l.getAircraft() != null && l.getAircraft().equals(aircraft)) {
						Date otherDeparture = l.getDeparture();
						Date otherArrival = l.getArrival();

						boolean overlap = MomentHelper.isBeforeOrEqual(departure, otherArrival) && MomentHelper.isBeforeOrEqual(otherDeparture, arrival);

						if (overlap)
							validAircraft = false;
					}
			}
			super.state(validAircraft, "*", "acme.validation.leg.invalid-aircraft.message");
		}

	}

	public static List<Leg> sortLegsByDeparture(final List<Leg> legs) {
		List<Leg> sortedLegs = new ArrayList<>(legs);
		sortedLegs.sort(Comparator.comparing(Leg::getDeparture));
		return sortedLegs;
	}

	@Override
	public void perform(final Leg leg) {
		leg.setIsDraftMode(false);  //publicado
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
		selectedAircraft.add("0", "----", leg.getAircraft() == null);

		Collection<Aircraft> aircraftsActives = this.repository.findAllAircrafts();

		List<Aircraft> finalAircrafts = aircraftsActives.stream().filter(a -> a.getAirline().getIATACode().equals(leg.getFlight().getManager().getAirline().getIATACode()) && a.getAircraftStatus() == AircraftStatus.ACTIVE).toList();

		for (Aircraft aircraft : finalAircrafts) {
			String key = String.valueOf(aircraft.getId());
			String label = aircraft.getRegitrationNumber();

			if (aircraft.getAirline() != null)
				label += " (" + aircraft.getAirline().getIATACode() + ")";

			boolean isSelected = aircraft.equals(leg.getAircraft());
			selectedAircraft.add(key, label, isSelected);
		}

		dataset = super.unbindObject(leg, "flightNumber", "departure", "arrival");
		dataset.put("status", choices);
		dataset.put("isDraftMode", leg.getIsDraftMode());
		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("aircrafts", selectedAircraft);
		dataset.put("aircraft", selectedAircraft.getSelected().getKey());
		dataset.put("isDraftFlight", leg.getFlight().getIsDraftMode());
		dataset.put("IATACode", leg.getFlight().getManager().getAirline().getIATACode());

		if (!airports.isEmpty()) {
			departureAirportChoices = SelectChoices.from(airports, "IATACode", leg.getDepartureAirport());
			arrivalAirportChoices = SelectChoices.from(airports, "IATACode", leg.getArrivalAirport());
			dataset.put("departureAirports", departureAirportChoices);
			dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
			dataset.put("arrivalAirports", arrivalAirportChoices);
			dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		}

		super.getResponse().addData(dataset);
	}

}
