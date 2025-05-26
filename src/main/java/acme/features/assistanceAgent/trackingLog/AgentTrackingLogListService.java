
package acme.features.assistanceAgent.trackingLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackinglog.TrackingLog;
import acme.entities.trackinglog.TrackingLogStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		Claim claim;
		int assistanceAgentId = super.getRequest().getPrincipal().getAccountId();
		if (super.getRequest().hasData("masterId")) {
			Integer claimId = super.getRequest().getData("masterId", Integer.class);
			if (claimId == null)
				status = false;
			else {
				claim = this.repository.findClaimById(claimId);
				List<TrackingLog> trackingLogs = this.repository.findAllByAssistanceAgentAndClaimId(assistanceAgentId, claimId);
				status = claim.getAssistanceAgent().getUserAccount().getId() == super.getRequest().getPrincipal().getAccountId();
			}
		}
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
		int masterId;
		Dataset dataset;
		masterId = super.getRequest().getData("masterId", int.class);
		List<TrackingLog> finishedTrackingLogs = new ArrayList<>();
		finishedTrackingLogs = this.repository.findTrackingLogsByClaimId(masterId).stream().filter(t -> !t.getStatus().equals(TrackingLogStatus.PENDING)).toList();
		boolean dontShowCreate;
		dontShowCreate = finishedTrackingLogs.stream().count() == 2;

		dataset = super.unbindObject(trackingLog, "lastUpdate", "step", "resolutionPercentage", "status", "resolution");
		super.getResponse().addData(dataset);

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("dontShowCreate", dontShowCreate);
	}

	@Override
	public void unbind(final Collection<TrackingLog> trackingLogs) {
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		super.getResponse().addGlobal("masterId", masterId);
	}
}
