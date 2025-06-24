
package acme.features.any.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.features.manager.leg.ManagerLegRepository;

@GuiService
public class AnyLegShowService extends AbstractGuiService<Any, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface ------------------------------------------


	@Override
	public void authorise() {

		int legId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.getFlightByLegId(legId);
		boolean isPublished = !flight.getIsDraftMode();
		super.getResponse().setAuthorised(isPublished);
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
		dataset = super.unbindObject(leg, "flightNumber", "departure", "arrival");
		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("isDraftMode", leg.getIsDraftMode());
		dataset.put("status", leg.getStatus().toString());
		dataset.put("aircraft", leg.getAircraft().getRegitrationNumber());
		dataset.put("duration", leg.getDuration());
		dataset.put("isDraftFlight", leg.getFlight().getIsDraftMode());
		dataset.put("departureAirport", leg.getDepartureAirport().getIATACode());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getIATACode());

		super.getResponse().addData(dataset);
	}
}
