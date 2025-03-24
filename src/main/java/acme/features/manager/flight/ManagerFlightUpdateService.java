
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Principal;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.components.ValidatorService;
import acme.entities.flight.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightUpdateService extends AbstractGuiService<Manager, Flight> {

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
		super.getResponse().setAuthorised(object.getManager().getUserAccount().getId() == userAccountId);
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
		super.bind(object);
	}

	@Override
	public void validate(final Flight object) {
		//		assert object != null;
		//				if (!super.getBuffer().getErrors().hasErrors("cost")) {
		//					super.state(this.service.validateLegSchedule(object.getCost()), "cost", "manager.project.form.error.cost");
		//				}
		//
		//		if (!super.getBuffer().getErrors().hasErrors("code")) {
		//			Flight exist;
		//			exist = this.repository.findProjectByCode(object.getCode());
		//			final Project p = object.getCode().equals("") || object.getCode().equals(null) ? null : this.repository.findProjectById(object.getId());
		//			super.state(p.equals(exist) || exist == null, "code", "manager.project.form.error.code");
		//		}

	}

	@Override
	public void perform(final Flight object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final Flight object) {
		assert object != null;
		Dataset dataset;
		dataset = super.unbindObject(object, "tag", "cost", "description", "requiresSelfTransfer", "description");
		super.getResponse().addData(dataset);
	}
}
