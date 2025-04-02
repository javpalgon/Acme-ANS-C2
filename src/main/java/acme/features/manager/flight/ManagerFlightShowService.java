
package acme.features.manager.flight;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Principal;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightShowService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	protected ManagerFlightRepository repository;


	@Override
	public void authorise() {
		Flight object;
		int id;
		id = super.getRequest().getData("id", int.class);
		object = this.repository.findFlightById(id);
		final Principal principal = super.getRequest().getPrincipal();
		final int userAccountId = principal.getAccountId();
		if (object.getIsDraftMode())
			super.getResponse().setAuthorised(object.getManager().getUserAccount().getId() == userAccountId);
		else
			super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Flight flight;
		int id;
		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);

		super.getBuffer().addData(flight);
	}

	@Override
	public void unbind(final Flight object) {
		assert object != null;
		Dataset dataset;
		dataset = super.unbindObject(object, "tag", "requiresSelfTransfer", "cost", "description", "isDraftMode");
		List<Leg> legs = this.repository.findLegsByFlightId(object.getId()).stream().toList();
		dataset.put("legs", !legs.isEmpty());
		if (!legs.isEmpty()) {
			dataset.put("departure", object.getDeparture());
			dataset.put("arrival", object.getArrival());
			dataset.put("originCity", object.getOriginCity());
			dataset.put("destinationCity", object.getDestinationCity());
			dataset.put("layovers", object.getNumOfLayovers());
		} else {
			dataset.put("departure", "null");
			dataset.put("arrival", "null");
			dataset.put("originCity", "null");
			dataset.put("destinationCity", "null");
			dataset.put("layovers", "null");
		}
		super.getResponse().addData(dataset);
	}

}
