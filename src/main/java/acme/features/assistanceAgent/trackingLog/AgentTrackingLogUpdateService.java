
package acme.features.assistanceAgent.trackingLog;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackinglog.TrackingLog;
import acme.entities.trackinglog.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AgentTrackingLogUpdateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int trackingLogId;
		Claim claim;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);

		trackingLog = this.repository.findTrackingLogById(trackingLogId);
		claim = this.repository.findClaimByTrackingLogId(trackingLogId);

		status = claim != null && super.getRequest().getPrincipal().getAccountId() == claim.getAssistanceAgent().getUserAccount().getId() && trackingLog.getIsDraftMode();
		if (status && super.getRequest().hasData("status"))
			status = this.checkStatusField();
		super.getResponse().setAuthorised(status);
	}

	private boolean checkStatusField() {
		String logStatus = super.getRequest().getData("status", String.class);
		return logStatus.equals("0") || logStatus.equals("PENDING") || logStatus.equals("ACCEPTED") || logStatus.equals("REJECTED");
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);
		trackingLog.setLastUpdate(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		int id = super.getRequest().getData("id", int.class);
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		assert trackingLog != null;
		TrackingLog original = this.repository.findTrackingLogById(trackingLog.getId());
		super.state(original.getIsDraftMode(), "*", "assistance-agent.tracking-log.form.error.you-cant-update-published-log");
		super.state(original.getClaim().getId() == trackingLog.getClaim().getId(), "*", "assistance-agent.tracking-log.form.error.cant-change-claim");

		double maximum = 0.;
		Claim claim = trackingLog.getClaim();
		List<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimId(claim.getId()).stream().filter(x -> x.getId() != trackingLog.getId()).toList();
		long countFinishedTrackingLogs = 0;
		if (trackingLogs != null)
			countFinishedTrackingLogs = trackingLogs.stream().filter(tl -> !tl.getIsDraftMode() && !tl.getStatus().equals(TrackingLogStatus.PENDING)).count();

		super.state(trackingLog.getResolutionPercentage() != null || trackingLog.getStatus() != null || trackingLog.getLastUpdate() != null, "*", "assistance-agent.tracking-log.form.error.value-null");

		super.state(claim != null, "*", "assistance-agent.tracking-log.form.error.claim-is-null");

		super.state(claim != null && !MomentHelper.isBefore(trackingLog.getLastUpdate(), claim.getRegisteredAt()), "*", "assistance-agent.tracking-log.form.error.lastUpdate-is-before-registeredAt");

		super.state(claim != null && !claim.getIsDraftMode(), "*", "assistance-agent.tracking-log.form.error.claim-is-not-published");

		super.state(trackingLog.getIsDraftMode(), "*", "assistance-agent.tracking-log.form.error.tracking-log-is-published");

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null) {
			super.state(trackingLog.getResolutionPercentage() < 100 && trackingLog.getStatus().equals(TrackingLogStatus.PENDING) || trackingLog.getResolutionPercentage() == 100 && !trackingLog.getStatus().equals(TrackingLogStatus.PENDING), "*",
				"assistance-agent.tracking-log.form.error.resolutionPercentage-and-status");

			if (!trackingLogs.isEmpty()) {
				TrackingLog optionalMax = trackingLogs.stream().filter(x -> x.getId() != trackingLog.getId()).max(Comparator.comparing(TrackingLog::getResolutionPercentage)).orElse(null);
				maximum = optionalMax == null ? 0.0 : optionalMax.getResolutionPercentage();
			}
			super.state(maximum == 0. || maximum == 100. || trackingLog.getResolutionPercentage() > maximum, "resolutionPercentage", "assistance-agent.tracking-log.form.error.resolutionPercentage");
		}

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null && trackingLog.getResolutionPercentage().equals(100.00) && !trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
			super.state(trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank(), "resolution", "assistance-agent.tracking-log.form.error.resolution-cant-be-null");

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null && !trackingLog.getResolutionPercentage().equals(100.00) && trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
			super.state(trackingLog.getResolution() == null || trackingLog.getResolution().isBlank(), "resolution", "assistance-agent.tracking-log.form.error.resolution-must-be-null");

		super.state(countFinishedTrackingLogs <= 1, "status", "assistance-agent.tracking-log.form.error.you-cant-create");

		if (trackingLogs != null) {
			List<TrackingLog> finishedPublishedTrackingLogs = trackingLogs.stream().filter(tl -> !tl.getStatus().equals(TrackingLogStatus.PENDING)).toList();
			super.state(finishedPublishedTrackingLogs.isEmpty() || finishedPublishedTrackingLogs.stream().allMatch(x -> x.getStatus().equals(trackingLog.getStatus())), "status", "assistance-agent.tracking-log.form.error.status-must-be-the-same");
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		assert trackingLog != null;
		this.repository.save(trackingLog);
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
