
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
		int masterId;
		Dataset dataset;

		masterId = super.getRequest().getData("masterId", int.class);
		dataset = super.unbindObject(trackingLog, "lastUpdate", "step", "resolutionPercentage", "status", "resolution");
		super.getResponse().addData(dataset);
		super.getResponse().addGlobal("masterId", masterId);
	}

	@Override
	public void unbind(final Collection<TrackingLog> trackingLogs) {
		int masterId;
		Claim claim;
		final boolean showCreate;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(masterId);
		showCreate = !claim.getIsDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}
}
