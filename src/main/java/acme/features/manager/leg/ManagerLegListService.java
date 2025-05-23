
package acme.features.manager.leg;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.realms.Manager;

@Repository
public class ManagerLegListService extends AbstractGuiService<Manager, Leg> {

	private static final Logger		logger	= LoggerFactory.getLogger(ManagerLegListService.class);

	@Autowired
	private ManagerLegRepository	repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
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
		assert leg != null;
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
		assert legs != null;
		int masterId;
		Flight flight;
		final boolean showCreate;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.repository.findFlightById(masterId);
		showCreate = flight.getIsDraftMode() && super.getRequest().getPrincipal().hasRealm(flight.getManager());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}
}
