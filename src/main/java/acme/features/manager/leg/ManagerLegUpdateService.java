
package acme.features.manager.leg;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
import acme.entities.leg.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

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
		super.state(leg.getStatus() != null, "status", "manager.leg.error.status-required");
		boolean validDeparture = true;
		Date departure = leg.getDeparture();
		Date arrival = leg.getArrival();
		Collection<Leg> allLegs = this.repository.findAllLegs();
		boolean isDuplicated = allLegs.stream().anyMatch(x -> x.getId() != leg.getId() && x.getFlightNumber().equals(leg.getFlightNumber()));
		if (isDuplicated)
			super.state(!isDuplicated, "flightNumber", "acme.validation.leg.duplicate-flight-number.message");
		if (arrival != null) {
			Date currentMoment = MomentHelper.getCurrentMoment();
			validDeparture = MomentHelper.isAfter(arrival, currentMoment);
			super.state(validDeparture, "departure", "acme.validation.leg.invalid-departure.message");
		}
		if (departure != null) {
			Date currentMoment = MomentHelper.getCurrentMoment();
			validDeparture = MomentHelper.isAfter(departure, currentMoment);
			super.state(validDeparture, "departure", "acme.validation.leg.invalid-departure.message");
		}

	}

	@Override
	public void perform(final Leg leg) {
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
		dataset.put("isDraftFlight", leg.getFlight().getIsDraftMode());
		dataset.put("IATACode", leg.getFlight().getManager().getAirline().getIATACode());
		departureAirportChoices = SelectChoices.from(airports, "IATACode", leg.getDepartureAirport());
		arrivalAirportChoices = SelectChoices.from(airports, "IATACode", leg.getArrivalAirport());
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalAirportChoices);
		dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		if (leg.getArrival() != null && leg.getDeparture() != null)
			dataset.put("duration", leg.getDuration());
		else
			dataset.put("duration", null);

		super.getResponse().addData(dataset);
	}
}
