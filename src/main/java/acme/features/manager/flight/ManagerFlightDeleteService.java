
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Principal;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

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
		super.getResponse().setAuthorised(object.getIsDraftMode() && object.getManager().getUserAccount().getId() == userAccountId);
	}

	@Override
	public void load() {
		Flight project;
		int id;

		id = super.getRequest().getData("id", int.class);
		project = this.repository.findFlightById(id);

		super.getBuffer().addData(project);
	}

	@Override
	public void bind(final Flight object) {
		assert object != null;
		int managerId;
		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		final Manager manager = this.repository.findOneManagerById(managerId);
		object.setManager(manager);
		super.bindObject(object, "tag", "cost", "description", "requiresSelfTransfer");
	}

	@Override
	public void validate(final Flight object) {
		assert object != null;
		if (!object.getIsDraftMode())
			super.state(object.getIsDraftMode(), "*", "manager.flight.form.error.notDraft", "isDraftMode");
	}
	@Override
	public void perform(final Flight object) {
		assert object != null;
		Collection<Leg> allLegs = this.repository.findLegsByFlightId(object.getId());
		for (Leg leg : allLegs)
			this.repository.delete(leg);
		this.repository.delete(object);
	}

	@Override
	public void unbind(final Flight object) {
		assert object != null;
		Dataset dataset;
		dataset = super.unbindObject(object, "tag", "cost", "description", "requiresSelfTransfer", "isDraftMode");
		super.getResponse().addData(dataset);
	}

}
