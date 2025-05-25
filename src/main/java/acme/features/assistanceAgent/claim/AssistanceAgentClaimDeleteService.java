
package acme.features.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimDeleteService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Claim claim;
		int claimId;
		int assistanceAgentId;

		claimId = super.getRequest().getData("id", int.class);
		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claim = this.repository.findClaimById(claimId);

		status = claim != null && claim.getIsDraftMode() == true && claim.getAssistanceAgent().getId() == assistanceAgentId && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

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
		super.bindObject(claim, "passengerEmail", "description", "type", "accepted");
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		super.state(claim != null && claim.getIsDraftMode().equals(true) && claim.getLeg().getIsDraftMode().equals(false), "leg", "assistance-agent.claim.form.error.leg-isDraftMode");
		super.state(claim != null && claim.getLeg() != null, "leg", "assistance-agent.claim.form.error.leg-null");
		super.state(claim != null && claim.getLeg() != null, "leg", "assistance-agent.claim.form.error.leg.flight-isDraftMode");
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		this.repository.deleteTrackingLogsByClaimId(claim.getId());
		this.repository.delete(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;
		AssistanceAgent agent;
		agent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		Dataset dataset = super.unbindObject(claim, "registeredAt", "passengerEmail", "description", "type", "accepted", "isDraftMode", "leg");
		dataset.put("type", SelectChoices.from(ClaimType.class, claim.getType()));
		dataset.put("leg", SelectChoices.from(this.repository.findPastPublishedLegsByAirline(MomentHelper.getCurrentMoment(), agent.getAirline()), "flightNumber", claim.getLeg()));
		super.getResponse().addData(dataset);
	}
}
