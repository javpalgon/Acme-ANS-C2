
package acme.features.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimStatus;
import acme.entities.leg.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimCreateService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Claim claim;
		int claimId;
		int assistanceAgentId;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);
		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();

		status = claim != null && claim.getAssistanceAgent().getUserAccount().getId() == assistanceAgentId;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim = new Claim();
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		claim.setRegisteredAt(MomentHelper.getCurrentMoment());
		claim.setAccepted(ClaimStatus.PENDING);
		claim.setAssistanceAgent(agent);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);
		super.bindObject(claim, "passengerEmail", "description", "type");
		claim.setLeg(leg);
		claim.setIsDraftMode(true);
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		super.state(claim.getLeg() == null || claim.getLeg().getArrival().after(claim.getRegisteredAt()), "leg", "assistance-agent.claim.form.error.leg-occurred");
		super.state(claim.getLeg().getIsDraftMode().equals(false), "leg", "assistance-agent.claim.form.leg-isDraftMode");
	}

	@Override
	public void perform(final Claim claim) {
		this.repository.save(claim);
	}
}
