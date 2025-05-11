
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
import acme.components.ValidatorService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	protected ManagerLegRepository	repository;

	@Autowired
	protected ValidatorService		service;


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
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.getLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		assert leg != null;
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
		//		assert leg != null;
		//
		//		// 1. Validate: arrival must be after departure
		//		if (leg.getDeparture() != null && leg.getArrival() != null && !leg.getArrival().after(leg.getDeparture()))
		//			super.state(false, "arrival", "acme.validation.leg.invalid-schedule.message");
		//
		// 2. Validate: departure and arrival airports must be different
		if (leg.getDepartureAirport() != null && leg.getArrivalAirport() != null && leg.getDepartureAirport().getId() == leg.getArrivalAirport().getId())
			super.state(false, "arrivalAirport", "acme.validation.leg.same-airports.message");

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
	public void perform(final Leg object) {
		assert object != null;
		this.repository.save(object);
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
		dataset.put("isDraftMode", leg.getIsDraftMode());
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
