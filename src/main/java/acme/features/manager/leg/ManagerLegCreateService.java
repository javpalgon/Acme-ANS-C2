
package acme.features.manager.leg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
public class ManagerLegCreateService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int flightId = super.getRequest().getData("masterId", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		boolean isOwner = flight != null && super.getRequest().getPrincipal().hasRealm(manager) && super.getRequest().getPrincipal().getAccountId() == flight.getManager().getUserAccount().getId();

		boolean isDraftFlight = flight != null && flight.getIsDraftMode();

		boolean status = isOwner && isDraftFlight;

		boolean validAircraft = true;
		boolean validAirport = true;

		if (super.getRequest().getMethod().equals("POST")) {

			Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
			Aircraft aircraft = aircraftId != null ? this.repository.findAircraftById(aircraftId) : null;
			boolean invalidAircraft = (aircraft == null || aircraft.getAircraftStatus() != AircraftStatus.ACTIVE) && aircraftId != null;
			if (invalidAircraft)
				validAircraft = false;

			Integer departureAirportId = super.getRequest().getData("departureAirport", Integer.class);
			Airport departureAirport = departureAirportId != null ? this.repository.findAirportById(departureAirportId) : null;
			boolean invalidDepartureAirport = departureAirport == null && departureAirportId != null;
			if (invalidDepartureAirport)
				validAirport = false;
			Integer arrivalAirportId = super.getRequest().getData("arrivalAirport", Integer.class);
			Airport arrivalAirport = arrivalAirportId != null ? this.repository.findAirportById(arrivalAirportId) : null;
			boolean invalidArrivalAirport = arrivalAirport == null && arrivalAirportId != null;
			if (invalidArrivalAirport)
				validAirport = false;
		}

		super.getResponse().setAuthorised(status && validAircraft && validAirport);
	}

	@Override
	public void load() {
		Leg leg;
		int masterId;
		Flight flight;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.repository.findFlightById(masterId);

		leg = new Leg();
		leg.setFlight(flight);
		leg.setIsDraftMode(true);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		int aircraftId;
		Aircraft aircraft;
		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findAircraftById(aircraftId);

		int departureAirportId;
		Airport departureAirport;
		departureAirportId = super.getRequest().getData("departureAirport", int.class);
		departureAirport = this.repository.findAirportById(departureAirportId);

		int arrivalAirportId;
		Airport arrivalAirport;
		arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		arrivalAirport = this.repository.findAirportById(arrivalAirportId);

		super.bindObject(leg, "flightNumber", "departure", "arrival", "status");
		leg.setAircraft(aircraft);
		leg.setDepartureAirport(departureAirport);
		leg.setArrivalAirport(arrivalAirport);
	}

	@Override
	public void validate(final Leg leg) {
		//		assert leg != null;
		//
		//		// 1. Validate: arrival must be after departure
		//		if (leg.getDeparture() != null && leg.getArrival() != null && !leg.getArrival().after(leg.getDeparture()))
		//			super.state(false, "arrival", "acme.validation.leg.invalid-schedule.message");
		//
		//		// 2. Validate: departure and arrival airports must be different
		//		if (leg.getDepartureAirport() != null && leg.getArrivalAirport() != null && leg.getDepartureAirport().getId() == leg.getArrivalAirport().getId())
		//			super.state(false, "arrivalAirport", "acme.validation.leg.same-airports.message");

		assert leg != null;
		super.state(leg.getStatus() != null, "status", "manager.leg.error.status-required");

		boolean validStatus = leg.getStatus() == LegStatus.ON_TIME || leg.getStatus() == LegStatus.DELAYED || leg.getStatus() == LegStatus.CANCELLED || leg.getStatus() == LegStatus.LANDED;
		super.state(validStatus, "status", "manager.leg.error.invalid-status");

		boolean validScheduledDeparture = true;
		Date scheduledDeparture = leg.getDeparture();
		Date currentMoment = MomentHelper.getCurrentMoment();
		if (scheduledDeparture != null)
			validScheduledDeparture = MomentHelper.isAfter(scheduledDeparture, currentMoment);
		super.state(validScheduledDeparture, "scheduledDeparture", "acme.validation.leg.invalid-departure.message");
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
		departureAirportChoices = SelectChoices.from(airports, "IATACode", leg.getDepartureAirport());
		arrivalAirportChoices = SelectChoices.from(airports, "IATACode", leg.getArrivalAirport());

		SelectChoices selectedAircraft;
		Collection<Aircraft> aircrafts;
		Collection<Aircraft> finalAircrafts = new ArrayList<>();
		aircrafts = this.repository.findAllActiveAircrafts(AircraftStatus.ACTIVE);
		for (Aircraft aircraft : aircrafts)
			if (aircraft.getAirline().getIATACode().equals(leg.getFlight().getManager().getAirline().getIATACode()))
				finalAircrafts.add(aircraft);
		selectedAircraft = SelectChoices.from(finalAircrafts, "regitrationNumber", leg.getAircraft());

		dataset = super.unbindObject(leg, "flightNumber", "departure", "arrival");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("isDraftMode", true);
		dataset.put("status", choices);
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalAirportChoices);
		dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		dataset.put("aircrafts", selectedAircraft);
		dataset.put("IATACode", leg.getFlight().getManager().getAirline().getIATACode());
		dataset.put("aircraft", selectedAircraft.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

}
