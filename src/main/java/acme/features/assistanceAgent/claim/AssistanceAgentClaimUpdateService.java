
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.leg.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimUpdateService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Claim claim;
		int id;
		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);
		status = claim.getAssistanceAgent().getUserAccount().getId() == super.getRequest().getPrincipal().getAccountId();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int id;
		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);
		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		assert claim != null;
		super.bindObject(claim, "passengerEmail", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		Claim original = this.repository.findClaimById(claim.getId());
		super.state(claim.getIsDraftMode(), "*", "assistance-agent.claim.form.error.notDraftMode", "isDraftMode");
		if (claim.getLeg() != null) {
			super.state(!claim.getLeg().getIsDraftMode(), "leg", "assistance-agent.claim.form.error.leg-isDraftMode");
			super.state(claim.getLeg().getDeparture().before(claim.getRegisteredAt()), "registeredAt", "assistance-agent.claim.form.error.registeredAt");
			if (claim.getLeg().getFlight() != null)
				super.state(!claim.getLeg().getFlight().getIsDraftMode(), "leg", "assistance-agent.claim.form.error.leg.flight-isDraftMode");
		}
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		int assistanceAgentId;
		Collection<Leg> legs;

		Dataset dataset = super.unbindObject(claim, "passengerEmail", "description", "type", "accepted", "isDraftMode", "leg");
		dataset.put("type", SelectChoices.from(ClaimType.class, claim.getType()));
		dataset.put("leg", SelectChoices.from(this.repository.findAllPublishedLegs(), "flightNumber", claim.getLeg()));
		super.getResponse().addData(dataset);
	}
}
