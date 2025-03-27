
package acme.features.manager.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.leg.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerLegDeleteService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	protected ManagerLegRepository repository;


	@Override
	public void authorise() {
		Leg object;
		//		int id;
		//		id = super.getRequest().getData("id", int.class);
		//		object = this.repository.findFlightById(id);
		//		final Principal principal = super.getRequest().getPrincipal();
		//		final int userAccountId = principal.getAccountId();
		//		super.getResponse().setAuthorised(object.getManager().getUserAccount().getId() == userAccountId);
	}

	@Override
	public void load() {
		//		Leg project;
		//		int id;
		//
		//		id = super.getRequest().getData("id", int.class);
		//		//		project = this.repository.findFlightById(id);
		//
		//		super.getBuffer().addData(project);
	}

	@Override
	public void bind(final Leg object) {
		//		assert object != null;
		//		int managerId;
		//		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		//		final Manager manager = this.repository.findOneManagerById(managerId);
		//		object.setManager(manager);
		//		super.bindObject(object, "tag", "cost", "description", "requiresSelfTransfer", "isDraftMode");
	}

	@Override
	public void validate(final Leg object) {
		assert object != null;
		if (!object.getIsDraftMode())
			super.state(object.getIsDraftMode(), "*", "manager.flight.form.error.notDraft", "isDraftMode");
	}
	@Override
	public void perform(final Leg object) {
		assert object != null;
		this.repository.delete(object);
	}

	@Override
	public void unbind(final Leg object) {
		assert object != null;
		Dataset dataset;
		dataset = super.unbindObject(object, "tag", "cost", "description", "requiresSelfTransfer", "isDraftMode");
		super.getResponse().addData(dataset);
	}

}
