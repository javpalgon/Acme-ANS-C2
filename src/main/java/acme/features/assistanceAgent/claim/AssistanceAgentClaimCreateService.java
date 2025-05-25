
package acme.features.assistanceAgent.claim;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
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
		int agentId;
		AssistanceAgent agent;
		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		agent = this.repository.findAgentById(agentId);
		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		if (status && super.getRequest().getMethod().equals("POST"))
			if (super.getRequest().hasData("leg", int.class)) {
				int legId = super.getRequest().getData("leg", int.class);
				if ((Integer) legId != null && legId != 0) {
					List<Leg> legsOk = (List<Leg>) this.repository.findPastPublishedLegsByAirline(MomentHelper.getCurrentMoment(), agent.getAirline());
					boolean legIsOk = legsOk.stream().anyMatch(l -> l.getId() == legId);
					if (!legIsOk || !this.checkStatusField())
						status = false;
				}
			}
		super.getResponse().setAuthorised(status);
	}

	private boolean checkStatusField() {
		String claimType = super.getRequest().getData("type", String.class);
		if (claimType != null && !claimType.equals(0))
			try {
				ClaimType.valueOf(claimType);
			} catch (IllegalArgumentException e) {
				return false;
			}
		return true;
	}
	@Override
	public void load() {
		Claim claim = new Claim();
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		claim.setRegisteredAt(MomentHelper.getCurrentMoment());
		claim.setAssistanceAgent(agent);
		claim.setIsDraftMode(true);
		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "passengerEmail", "description", "type", "accepted", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		if (claim.getLeg() != null) {
			super.state(!claim.getLeg().getIsDraftMode(), "leg", "assistance-agent.claim.form.error.leg-isDraftMode");
			super.state(claim.getLeg().getArrival().before(claim.getRegisteredAt()), "*", "assistance-agent.claim.form.error.registeredAt");
			super.state(claim.getLeg().getFlight() != null && !claim.getLeg().getFlight().getIsDraftMode(), "leg", "assistance-agent.claim.form.error.leg.flight-isDraftMode");
		}
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		AssistanceAgent agent;
		agent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		Dataset dataset = super.unbindObject(claim, "passengerEmail", "description", "type", "accepted", "isDraftMode", "leg");
		dataset.put("type", SelectChoices.from(ClaimType.class, claim.getType()));

		dataset.put("leg", SelectChoices.from(this.repository.findPastPublishedLegsByAirline(MomentHelper.getCurrentMoment(), agent.getAirline()), "flightNumber", claim.getLeg()));
		dataset.put("isDraftMode", true);
		super.getResponse().addData(dataset);
	}
}
