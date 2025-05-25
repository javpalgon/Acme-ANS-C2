
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.realms.Manager;

@Repository
public class ManagerLegListService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		int masterId;
		Flight flight;
		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.repository.findFlightById(masterId);
		boolean isOwner = super.getRequest().getPrincipal().getAccountId() == flight.getManager().getUserAccount().getId();
		super.getResponse().setAuthorised(isOwner);

	}

	@Override
	public void load() {
		Collection<Leg> legs;
		int masterId;
		masterId = super.getRequest().getData("masterId", int.class);
		legs = this.repository.findLegsByFlightId(masterId);
		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;
		dataset = super.unbindObject(leg, "flightNumber", "departure", "arrival", "status");
		dataset.put("arrivalAirport", leg.getArrivalAirport().getIATACode());
		dataset.put("departureAirport", leg.getDepartureAirport().getIATACode());
		dataset.put("aircraft", leg.getAircraft().getModel());
		super.addPayload(dataset, leg, "flightNumber", "departure", "arrival", "status", "departureAirport", "arrivalAirport", "isDraftMode", "aircraft");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Leg> legs) {
		int masterId;
		Flight flight;
		final boolean showCreateButton;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.repository.findFlightById(masterId);
		showCreateButton = flight.getIsDraftMode() && super.getRequest().getPrincipal().hasRealm(flight.getManager());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreateButton);
	}
}
