
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
public class ManagerLegPublishService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

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
		assert leg != null;

		boolean validScheduledDeparture = true;
		Date scheduledDeparture = leg.getDeparture();
		Date currentMoment = MomentHelper.getCurrentMoment();
		if (scheduledDeparture != null)
			validScheduledDeparture = MomentHelper.isAfter(scheduledDeparture, currentMoment);
		super.state(validScheduledDeparture, "departure", "acme.validation.leg.invalid-departure.message");
	}

	@Override
	public void perform(final Leg leg) {
		assert leg != null;
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
		selectedAircraft.add("0", "----", leg.getAircraft() == null);

		Collection<Aircraft> aircraftsActives = this.repository.findAllActiveAircrafts(AircraftStatus.ACTIVE);
		Collection<Aircraft> finalAircrafts = new ArrayList<Aircraft>();
		for (Aircraft aircraft : aircraftsActives)
			if (aircraft.getAirline().getIATACode().equals(leg.getFlight().getManager().getAirline().getIATACode()))
				finalAircrafts.add(aircraft);

		for (Aircraft aircraft : finalAircrafts) {
			String key = Integer.toString(aircraft.getId());
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
