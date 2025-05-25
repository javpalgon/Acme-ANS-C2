
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
public class AssistanceAgentClaimShowService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		Claim claim;
		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);
		status = super.getRequest().getPrincipal().getAccountId() == claim.getAssistanceAgent().getUserAccount().getId() && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
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
	public void unbind(final Claim claim) {
		assert claim != null;
		AssistanceAgent agent;
		agent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		Dataset dataset;
		dataset = super.unbindObject(claim, "registeredAt", "passengerEmail", "description", "type", "accepted", "isDraftMode", "leg");
		dataset.put("arrival", claim.getLeg().getArrival());
		dataset.put("departure", claim.getLeg().getDeparture());
		dataset.put("status", claim.getLeg().getStatus());

		SelectChoices typeChoices = SelectChoices.from(ClaimType.class, claim.getType());
		dataset.put("type", typeChoices);
		dataset.put("leg", SelectChoices.from(this.repository.findPastPublishedLegsByAirline(MomentHelper.getCurrentMoment(), agent.getAirline()), "flightNumber", claim.getLeg()));

		super.getResponse().addData(dataset);
	}
}
