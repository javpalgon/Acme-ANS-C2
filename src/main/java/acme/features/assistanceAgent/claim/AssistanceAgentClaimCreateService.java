
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimStatus;
import acme.entities.claim.ClaimType;
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
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Claim claim = new Claim();
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		claim.setRegisteredAt(MomentHelper.getCurrentMoment());
		claim.setAccepted(ClaimStatus.PENDING);
		claim.setAssistanceAgent(agent);
		claim.setIsDraftMode(true);
		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		String flightNumber;
		Leg leg;
		super.bindObject(claim, "passengerEmail", "description", "type", "accepted", "leg");

		//flightNumber = super.getRequest().getData("leg", String.class);
		//		 leg = this.repository.findLegByFlightNumber(flightNumber);
		//		 claim.setLeg(leg);

	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		super.state(claim.getLeg() != null && claim.getLeg().getIsDraftMode().equals(false), "leg", "assistance-agent.claim.form.error.leg-isDraftMode");
		super.state(claim.getLeg() != null && claim.getLeg().getDeparture().before(claim.getRegisteredAt()), "registeredAt", "assistance-agent.claim.form.error.registeredAt");
		super.state(claim.getLeg() != null && !claim.getLeg().getIsDraftMode() && claim.getLeg().getFlight() != null && !claim.getLeg().getFlight().getIsDraftMode(), "leg", "assistance-agent.claim.form.error.leg.flight-isDraftMode");
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

		Dataset dataset = super.unbindObject(claim, "registeredAt", "passengerEmail", "description", "type", "accepted", "isDraftMode", "leg");
		dataset.put("type", SelectChoices.from(ClaimType.class, claim.getType()));
		dataset.put("leg", SelectChoices.from(this.repository.findAllPublishedLegs(), "flightNumber", claim.getLeg()));
		super.getResponse().addData(dataset);
	}
}
