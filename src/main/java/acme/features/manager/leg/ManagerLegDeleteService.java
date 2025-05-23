
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.leg.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerLegDeleteService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	protected ManagerLegRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.getLegById(id);
		boolean authorised = leg != null && super.getRequest().getPrincipal().hasRealm(leg.getFlight().getManager()) && leg.getIsDraftMode();
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void bind(final Leg leg) {
		assert leg != null;
		;
	}

	@Override
	public void validate(final Leg leg) {
		assert leg != null;
		;
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.getLegById(id);
		super.getBuffer().addData(leg);
	}

	@Override
	public void perform(final Leg leg) {
		assert leg != null;
		this.repository.delete(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		assert leg != null;
		Dataset dataset;

		SelectChoices departureAirportChoices;
		SelectChoices arrivalAirportChoices;
		Collection<Airport> airports;
		airports = this.repository.findAllAirports();

		dataset = super.unbindObject(leg, "flightNumber", "departure", "arrival", "status", "departureAirport", "arrivalAirport", "aircraft");
		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("isDraftMode", leg.getIsDraftMode());
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

	@Override
	public void onSuccess() {
		PrincipalHelper.handleUpdate();
	}
}
