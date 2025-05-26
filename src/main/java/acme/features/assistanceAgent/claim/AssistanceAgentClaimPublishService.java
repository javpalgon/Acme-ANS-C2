
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
public class AssistanceAgentClaimPublishService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		Claim claim;
		int agentId;
		AssistanceAgent agent;
		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		agent = this.repository.findAgentById(agentId);
		Integer id;
		if (super.getRequest().hasData("id")) {
			id = super.getRequest().getData("id", Integer.class);
			if (id != null) {
				claim = this.repository.findClaimById(id);
				status = claim.getAssistanceAgent().getUserAccount().getId() == super.getRequest().getPrincipal().getAccountId() && claim.getIsDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
			}
		}
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
		int assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		AssistanceAgent agent = this.repository.findAgentById(assistanceAgentId);
		Integer legId = super.getRequest().getData("leg", int.class);
		if (legId != null)
			claim.setLeg(this.repository.findLegById(legId));
		claim.setAssistanceAgent(agent);
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		Claim original = this.repository.findClaimById(claim.getId());
		if (claim.getLeg() != null) {
			super.state(claim.getLeg().getFlightNumber() != null && claim.getLeg().getFlightNumber().equals(original.getLeg().getFlightNumber()), "leg", "assistance-agent.claim.form.error.save-changes");
			super.state(!claim.getLeg().getIsDraftMode(), "leg", "assistance-agent.claim.form.error.leg-isDraftMode");
			super.state(claim.getLeg().getDeparture().before(claim.getRegisteredAt()), "registeredAt", "assistance-agent.claim.form.error.registeredAt");
			if (claim.getLeg().getFlight() != null)
				super.state(!claim.getLeg().getFlight().getIsDraftMode(), "leg", "assistance-agent.claim.form.error.leg.flight-isDraftMode");
		}
		super.state(claim.getDescription() != null && claim.getDescription().equals(original.getDescription()), "description", "assistance-agent.claim.form.error.save-changes");
		super.state(claim.getPassengerEmail() != null && claim.getPassengerEmail().equals(original.getPassengerEmail()), "passengerEmail", "assistance-agent.claim.form.error.save-changes");
		super.state(claim.getType() != null && claim.getType().equals(original.getType()), "type", "assistance-agent.claim.form.error.save-changes");
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		claim.setIsDraftMode(false);
		this.repository.save(claim);
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
