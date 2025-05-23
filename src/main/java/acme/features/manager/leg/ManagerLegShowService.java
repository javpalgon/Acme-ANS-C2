
package acme.features.manager.leg;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;
import acme.features.manager.flight.ManagerFlightRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegShowService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private ManagerFlightRepository	repositoryFlight;

	// AbstractGuiService interface ------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int legId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.getFlightByLegId(legId);

		status = flight != null && (!flight.getIsDraftMode() || super.getRequest().getPrincipal().hasRealm(flight.getManager()) && super.getRequest().getPrincipal().getAccountId() == flight.getManager().getUserAccount().getId());

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
		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("isDraftMode", leg.getIsDraftMode());
		dataset.put("status", choices);
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
