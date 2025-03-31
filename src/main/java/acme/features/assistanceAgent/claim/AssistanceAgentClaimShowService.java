
package acme.features.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
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
		status = super.getRequest().getPrincipal().getAccountId() == claim.getAssistanceAgent().getUserAccount().getId();
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
		Dataset dataset;
		dataset = super.unbindObject(claim, "registeredAt", "passengerEmail", "description", "type", "accepted", "isDraftMode");
		dataset.put("leg.flightNumber", claim.getLeg().getFlightNumber());
		dataset.put("leg.arrival", claim.getLeg().getArrival());
		dataset.put("leg.departure", claim.getLeg().getDeparture());
		dataset.put("leg.status", claim.getLeg().getStatus());
		super.getResponse().addData(dataset);
	}
}
