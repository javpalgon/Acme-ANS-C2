
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

		boolean authorised = leg != null && super.getRequest().getPrincipal().hasRealm(leg.getFlight().getManager());
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

	//	@Override
	//	public void bind(final Leg leg) {
	//		int aircraftId;
	//		Aircraft aircraft;
	//		aircraftId = super.getRequest().getData("aircraft", int.class);
	//		aircraft = this.repository.findAircraftById(aircraftId);
	//
	//		int departureAirportId;
	//		Airport departureAirport;
	//		departureAirportId = super.getRequest().getData("departureAirport", int.class);
	//		departureAirport = this.repository.findAirportById(departureAirportId);
	//
	//		int arrivalAirportId;
	//		Airport arrivalAirport;
	//		arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
	//		arrivalAirport = this.repository.findAirportById(arrivalAirportId);
	//
	//		super.bindObject(leg, "flightNumber", "departure", "arrival", "status");
	//		leg.setAircraft(aircraft);
	//		leg.setDepartureAirport(departureAirport);
	//		leg.setArrivalAirport(arrivalAirport);
	//	}

	@Override
	public void bind(final Leg leg) {
		int id = super.getRequest().getData("id", int.class);
		boolean isDraftMode = this.repository.getLegById(id).getIsDraftMode();

		if (isDraftMode) {
			// Si está en draft, bind completo
			super.bindObject(leg, "flightNumber", "departure", "arrival", "status");

			int aircraftId = super.getRequest().getData("aircraft", int.class);
			Aircraft aircraft = this.repository.findAircraftById(aircraftId);
			leg.setAircraft(aircraft);

			int departureAirportId = super.getRequest().getData("departureAirport", int.class);
			Airport departureAirport = this.repository.findAirportById(departureAirportId);
			leg.setDepartureAirport(departureAirport);

			int arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
			Airport arrivalAirport = this.repository.findAirportById(arrivalAirportId);
			leg.setArrivalAirport(arrivalAirport);
		} else {
			// Si ya está publicado, solo bind del status
			super.bindObject(leg, "status");

			// Cargar los valores originales para evitar que se pierdan (por si acaso)
			Leg original = this.repository.getLegById(leg.getId());
			leg.setFlightNumber(original.getFlightNumber());
			leg.setDeparture(original.getDeparture());
			leg.setArrival(original.getArrival());
			leg.setAircraft(original.getAircraft());
			leg.setDepartureAirport(original.getDepartureAirport());
			leg.setArrivalAirport(original.getArrivalAirport());
		}
	}

	@Override
	public void validate(final Leg leg) {
		assert leg != null;

		Leg original = this.repository.getLegById(leg.getId());

		if (!original.getIsDraftMode()) {
			// Solo permitimos cambiar el status
			if (!original.getStatus().equals(leg.getStatus())) {
				// Está cambiando status, OK
			} else
				super.state(false, "status", "manager.leg.error.must-change-status");

			// Bloqueamos cualquier otro cambio
			if (!original.getFlightNumber().equals(leg.getFlightNumber()) || !original.getDeparture().equals(leg.getDeparture()) || !original.getArrival().equals(leg.getArrival()) || original.getAircraft().getId() != leg.getAircraft().getId()
				|| original.getDepartureAirport().getId() != leg.getDepartureAirport().getId() || original.getArrivalAirport().getId() != leg.getArrivalAirport().getId())
				super.state(false, "*", "manager.leg.error.only-status-can-change");

		}
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
