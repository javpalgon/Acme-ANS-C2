
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.components.ValidatorService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.airport.Airport;
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
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.getLegById(id);

		boolean authorised = leg != null && super.getRequest().getPrincipal().hasRealm(leg.getFlight().getManager()) && leg.getIsDraftMode();

		super.getResponse().setAuthorised(authorised);
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
	public void validate(final Leg object) {
		assert object != null;
		;

	}

	@Override
	public void perform(final Leg object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final Leg leg) {
		//		if (leg.getIsDraftMode()) {
		assert leg != null;
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
		aircrafts = this.repository.findAllActiveAircrafts(AircraftStatus.ACTIVE);
		// aircrafts = this.repository.findAllAircrafts();
		selectedAircraft = SelectChoices.from(aircrafts, "regitrationNumber", leg.getAircraft());
		dataset = super.unbindObject(leg, "flightNumber", "departure", "arrival");
		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("isDraftMode", leg.getFlight().getIsDraftMode());
		dataset.put("status", choices);
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalAirportChoices);
		dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		dataset.put("aircrafts", selectedAircraft);
		dataset.put("aircraft", selectedAircraft.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
