
package acme.features.assistanceAgent.trackingLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.trackinglog.TrackingLog;
import acme.entities.trackinglog.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int assistanceAgentId = super.getRequest().getPrincipal().getAccountId();
		int claimId = super.getRequest().getData("masterId", int.class);
		List<TrackingLog> trackingLogs = this.repository.findAllByAssistanceAgentAndClaimId(assistanceAgentId, claimId);
		status = trackingLogs.stream().allMatch(x -> x.getClaim().getId() == claimId && x.getClaim().getAssistanceAgent().getId() == assistanceAgentId);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<TrackingLog> trackingLogs = new ArrayList<>();
		int assistanceAgentId;
		int claimId;
		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claimId = super.getRequest().getData("masterId", int.class);
		trackingLogs = this.repository.findAllByAssistanceAgentAndClaimId(assistanceAgentId, claimId);
		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		dataset = super.unbindObject(trackingLog, "lastUpdate", "step", "resolutionPercentage", "status", "resolution");
		SelectChoices statusChoices = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		super.getResponse().addData(dataset);
	}
}
