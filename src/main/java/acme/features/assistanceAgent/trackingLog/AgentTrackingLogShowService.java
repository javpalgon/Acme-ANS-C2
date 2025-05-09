
package acme.features.assistanceAgent.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackinglog.TrackingLog;
import acme.entities.trackinglog.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AgentTrackingLogShowService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int trackingLogId;
		Claim claim;

		trackingLogId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimByTrackingLogId(trackingLogId);

		status = super.getRequest().getPrincipal().getAccountId() == claim.getAssistanceAgent().getUserAccount().getId();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int trackingLogId = super.getRequest().getData("id", int.class);
		super.getBuffer().addData(this.repository.findTrackingLogById(trackingLogId));
	}

	@Override
	public void unbind(final TrackingLog object) {
		assert object != null;
		Dataset dataset = super.unbindObject(object, "lastUpdate", "step", "resolutionPercentage", "status", "resolution", "isDraftMode");
		SelectChoices statusChoices = SelectChoices.from(TrackingLogStatus.class, object.getStatus());
		dataset.put("status", statusChoices);
		dataset.put("passengerEmail", object.getClaim().getPassengerEmail());
		dataset.put("description", object.getClaim().getDescription());
		dataset.put("masterId", object.getClaim().getId());
		super.getResponse().addData(dataset);
	}
}
