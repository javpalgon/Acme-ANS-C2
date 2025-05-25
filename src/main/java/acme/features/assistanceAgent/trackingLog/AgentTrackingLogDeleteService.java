
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
public class AgentTrackingLogDeleteService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int trackingLogId;
		Claim claim;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);

		claim = this.repository.findClaimByTrackingLogId(trackingLogId);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);

		status = claim != null && super.getRequest().getPrincipal().getAccountId() == claim.getAssistanceAgent().getUserAccount().getId() && trackingLog.getIsDraftMode();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {

	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		assert trackingLog != null;
		super.state(trackingLog.getIsDraftMode(), "*", "assistance-agent.trackingLog.form.error.delete-published-log");
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		TrackingLog trackingLog = this.repository.findTrackingLogById(id);
		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		assert trackingLog != null;
		this.repository.delete(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		assert trackingLog != null;
		Dataset dataset = super.unbindObject(trackingLog, "lastUpdate", "step", "resolutionPercentage", "status", "resolution", "isDraftMode");
		SelectChoices statusChoices = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("status", statusChoices);
		dataset.put("passengerEmail", trackingLog.getClaim().getPassengerEmail());
		dataset.put("description", trackingLog.getClaim().getDescription());
		dataset.put("masterId", trackingLog.getClaim().getId());
		super.getResponse().addData(dataset);
	}

}
