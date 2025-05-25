
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
public class AgentTrackingLogPublishService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

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
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int trackingLogId;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);

		Claim claim;

		trackingLogId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimByTrackingLogId(trackingLogId);
		trackingLog.setClaim(claim);
		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		assert trackingLog != null;
		double maximum = 0.;
		TrackingLog original = this.repository.findTrackingLogById(trackingLog.getId());
		Claim claim = trackingLog.getClaim();
		List<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimId(claim.getId()).stream().filter(x -> x.getId() != trackingLog.getId()).toList();
		long countFinishedTrackingLogs = 0;
		if (trackingLogs != null)
			countFinishedTrackingLogs = trackingLogs.stream().filter(tl -> !tl.getIsDraftMode() && !tl.getStatus().equals(TrackingLogStatus.PENDING)).count();

		super.state(trackingLog.getResolutionPercentage() != null || trackingLog.getStatus() != null || trackingLog.getLastUpdate() != null, "*", "assistance-agent.tracking-log.form.error.value-null");

		super.state(claim != null, "*", "assistance-agent.tracking-log.form.error.claim-is-null");

		super.state(claim != null && !MomentHelper.isBefore(trackingLog.getLastUpdate(), claim.getRegisteredAt()), "*", "assistance-agent.tracking-log.form.error.lastUpdate-is-before-registeredAt");

		super.state(claim != null && !claim.getIsDraftMode(), "*", "assistance-agent.tracking-log.form.error.claim-is-not-published");

		super.state(original.getIsDraftMode(), "*", "assistance-agent.tracking-log.form.error.tracking-log-is-published");

		if (trackingLogs != null) {
			TrackingLog optionalMax = trackingLogs.stream().filter(x -> x.getId() != trackingLog.getId() && !x.getIsDraftMode()).max(Comparator.comparing(TrackingLog::getResolutionPercentage)).orElse(null);
			maximum = optionalMax == null ? 0.0 : optionalMax.getResolutionPercentage();

		}
		if (trackingLog.getResolutionPercentage() != null)
			super.state(maximum == 0. || trackingLog.getResolutionPercentage() == 100. || trackingLog.getResolutionPercentage() > maximum, "resolutionPercentage", "assistance-agent.tracking-log.form.error.resolutionPercentage");

		super.state(trackingLog.getStep() != null && trackingLog.getStep().equals(original.getStep()), "step", "assistance-agent.tracking-log.form.error.save-changes");

		super.state(trackingLog.getResolutionPercentage() != null && trackingLog.getResolutionPercentage().equals(original.getResolutionPercentage()), "resolutionPercentage", "assistance-agent.tracking-log.form.error.save-changes");

		super.state(trackingLog.getStatus() != null && trackingLog.getStatus().equals(original.getStatus()), "status", "assistance-agent.tracking-log.form.error.save-changes");

		if (trackingLog.getResolution().equals("")) {
			if (original.getResolution() != null)
				super.state(original.getResolution().isBlank(), "resolution", "assistance-agent.tracking-log.form.error.save-changes");
		} else
			super.state(trackingLog.getResolution().equals(original.getResolution()), "resolution", "assistance-agent.tracking-log.form.error.save-changes");

		if (trackingLogs != null)
			super.state(trackingLogs.stream().filter(x -> x.getIsDraftMode() && x.getResolutionPercentage() < trackingLog.getResolutionPercentage()).count() == 0, "*", "assistance-agent.tracking-log.form.error.delete-or-publish-previous-logs");

	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		assert trackingLog != null;
		trackingLog.setIsDraftMode(false);
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		assert trackingLog != null;

		Dataset dataset = super.unbindObject(trackingLog, "lastUpdate", "step", "resolutionPercentage", "status", "resolution", "isDraftMode");
		SelectChoices statusChoices = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset.put("passengerEmail", trackingLog.getClaim().getPassengerEmail());
		dataset.put("description", trackingLog.getClaim().getDescription());
		dataset.put("status", statusChoices);
		super.getResponse().addData(dataset);
	}
}
