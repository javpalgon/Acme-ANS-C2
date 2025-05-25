
package acme.features.assistanceAgent.trackingLog;

import java.util.ArrayList;
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
public class AgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;
		int countFinishedTrackingLogs = 0;
		List<TrackingLog> trackingLogs = new ArrayList<>();

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(masterId);
		trackingLogs = this.repository.findTrackingLogsByClaimId(masterId).stream().toList();
		countFinishedTrackingLogs = trackingLogs.stream().filter(tl -> !tl.getStatus().equals(TrackingLogStatus.PENDING)).toList().size();
		status = super.getRequest().getPrincipal().getAccountId() == claim.getAssistanceAgent().getUserAccount().getId();
		if (status && countFinishedTrackingLogs >= 2)
			status = false;
		else if (status && super.getRequest().hasData("status"))
			status = this.checkStatusField();
		super.getResponse().setAuthorised(status);
	}

	private boolean checkStatusField() {
		String logStatus = super.getRequest().getData("status", String.class);
		return logStatus.equals("0") || logStatus.equals("PENDING") || logStatus.equals("ACCEPTED") || logStatus.equals("REJECTED");
	}
	@Override
	public void load() {
		Claim claim;
		TrackingLog trackingLog = new TrackingLog();
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(masterId);

		trackingLog.setLastUpdate(MomentHelper.getCurrentMoment());
		trackingLog.setIsDraftMode(true);
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
		Claim claim = trackingLog.getClaim();
		List<TrackingLog> trackingLogs = (List<TrackingLog>) this.repository.findTrackingLogsByClaimId(claim.getId());
		long countFinishedTrackingLogs = 0;
		if (trackingLogs != null)
			countFinishedTrackingLogs = trackingLogs.stream().filter(tl -> !tl.getStatus().equals(TrackingLogStatus.PENDING)).count();

		super.state(trackingLog.getResolutionPercentage() != null || trackingLog.getStatus() != null || trackingLog.getLastUpdate() != null, "*", "assistance-agent.tracking-log.form.error.value-null");

		super.state(claim != null, "*", "assistance-agent.tracking-log.form.error.claim-is-null");

		super.state(claim != null && !MomentHelper.isBefore(trackingLog.getLastUpdate(), claim.getRegisteredAt()), "*", "assistance-agent.tracking-log.form.error.lastUpdate-is-before-registeredAt");

		super.state(trackingLog.getIsDraftMode(), "*", "assistance-agent.tracking-log.form.error.tracking-log-is-published");

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null) {
			super.state(trackingLog.getResolutionPercentage() < 100 && trackingLog.getStatus().equals(TrackingLogStatus.PENDING) || trackingLog.getResolutionPercentage() == 100 && !trackingLog.getStatus().equals(TrackingLogStatus.PENDING), "*",
				"assistance-agent.tracking-log.form.error.resolutionPercentage-and-status");

			if (!trackingLogs.isEmpty()) {
				TrackingLog optionalMax = trackingLogs.stream().max(Comparator.comparing(TrackingLog::getResolutionPercentage)).orElse(null);
				maximum = optionalMax == null ? 0.0 : optionalMax.getResolutionPercentage();
			}
			super.state(maximum == 0. || trackingLog.getResolutionPercentage() == 100. || trackingLog.getResolutionPercentage() > maximum, "resolutionPercentage", "assistance-agent.tracking-log.form.error.resolutionPercentage");
		}

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null && trackingLog.getResolutionPercentage().equals(100.00) && !trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
			super.state(trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank(), "resolution", "assistance-agent.tracking-log.form.error.resolution-cant-be-null");

		if (trackingLog.getResolutionPercentage() != null && trackingLog.getStatus() != null && !trackingLog.getResolutionPercentage().equals(100.00) && trackingLog.getStatus().equals(TrackingLogStatus.PENDING))
			super.state(trackingLog.getResolution() == null || trackingLog.getResolution().isBlank(), "resolution", "assistance-agent.tracking-log.form.error.resolution-must-be-null");

		if (trackingLogs != null) {
			List<TrackingLog> finishedPublishedTrackingLogs = trackingLogs.stream().filter(tl -> !tl.getStatus().equals(TrackingLogStatus.PENDING)).toList();
			super.state(finishedPublishedTrackingLogs.isEmpty() || finishedPublishedTrackingLogs.stream().allMatch(x -> x.getStatus().equals(trackingLog.getStatus())), "status", "assistance-agent.tracking-log.form.error.status-must-be-the-same");
			super.state(finishedPublishedTrackingLogs.stream().filter(x -> x.getIsDraftMode()).count() < 1, "*", "assistance-agent.tracking-log.form.error.you-cant-create");
			//super.state(finishedPublishedTrackingLogs.stream().filter(x -> !x.getIsDraftMode()).count() < 1, "*", "assistance-agent.tracking-log.form.error.you-cant-create");
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		assert trackingLog != null;
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset = super.unbindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("status", SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus()));
		dataset.put("passengerEmail", trackingLog.getClaim().getPassengerEmail());
		dataset.put("description", trackingLog.getClaim().getDescription());
		dataset.put("isDraftMode", true);
		super.getResponse().addData(dataset);
	}
}
