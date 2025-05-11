
package acme.features.manager.flight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Principal;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.components.ValidatorService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	protected ManagerFlightRepository	repository;

	@Autowired
	protected ValidatorService			service;


	@Override
	public void authorise() {
		Flight object;
		int id;
		id = super.getRequest().getData("id", int.class);
		object = this.repository.findFlightById(id);
		final Principal principal = super.getRequest().getPrincipal();
		final int userAccountId = principal.getAccountId();
		super.getResponse().setAuthorised(object.getIsDraftMode() && object.getManager().getUserAccount().getId() == userAccountId);
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
	public void bind(final Flight object) {
		assert object != null;
		super.bindObject(object, "tag", "cost", "description", "requiresSelfTransfer");
	}

	@Override
	public void validate(final Flight object) {
		assert object != null;

		Collection<Leg> legs = this.repository.findLegsByFlightId(object.getId());

		// 1. Validar que el vuelo tenga al menos una leg
		super.state(!legs.isEmpty(), "*", "manager.flight.form.error.legsEmpty");

		List<Leg> sortedLegs = new ArrayList<>(legs);
		sortedLegs.sort(Comparator.comparing(Leg::getDeparture));

		boolean valid = true;

		for (int i = 0; i < sortedLegs.size(); i++) {
			Leg current = sortedLegs.get(i);

			// 2. Todas las legs deben estar publicadas
			if (current.getIsDraftMode()) {
				super.state(false, "*", "manager.flight.form.error.LegsNotPublished");
				valid = false;
			}

			// 3. Horario válido en cada leg
			if (!current.getArrival().after(current.getDeparture())) {
				super.state(false, "*", "acme.validation.leg.invalid-schedule.message");
				valid = false;
			}

			// 4. Aeropuertos distintos en cada leg
			if (current.getArrivalAirport().getId() == current.getDepartureAirport().getId()) {
				super.state(false, "*", "acme.validation.leg.same-airports.message");
				valid = false;
			}

			// 5. Comparación entre legs consecutivas
			if (i < sortedLegs.size() - 1) {
				Leg next = sortedLegs.get(i + 1);

				// No se deben solapar
				if (!current.getArrival().before(next.getDeparture())) {
					super.state(false, "*", "acme.validation.leg.invalid-timing-sequence.message");
					valid = false;
				}

				// Aeropuertos deben conectar
				if (!current.getArrivalAirport().equals(next.getDepartureAirport())) {
					super.state(false, "*", "acme.validation.leg.inconsistent-airport-connection.message");
					valid = false;
				}

				// No puede haber dos salidas iguales
				if (current.getDeparture().equals(next.getDeparture())) {
					super.state(false, "*", "acme.validation.leg.same-departure.message");
					valid = false;
				}
			}
		}
	}

	@Override
	public void perform(final Flight object) {
		assert object != null;
		object.setIsDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Flight object) {
		assert object != null;
		Dataset dataset;
		dataset = super.unbindObject(object, "tag", "cost", "description", "requiresSelfTransfer", "description", "isDraftMode");
		super.getResponse().addData(dataset);
	}

}
