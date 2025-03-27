
package acme.features.manager.leg;

import org.springframework.beans.factory.annotation.Autowired;

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
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.getLegById(id);

		boolean authorised = leg != null && super.getRequest().getPrincipal().hasRealm(leg.getFlight().getManager()) && leg.getIsDraftMode();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void bind(final Leg leg) {
		;
	}

	@Override
	public void validate(final Leg leg) {
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
		this.repository.delete(leg);
	}
}
