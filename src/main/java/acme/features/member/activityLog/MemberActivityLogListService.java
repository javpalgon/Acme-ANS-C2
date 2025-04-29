
package acme.features.member.activityLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.assignment.Assignment;
import acme.entities.leg.LegStatus;
import acme.realms.Member;

@GuiService
public class MemberActivityLogListService extends AbstractGuiService<Member, ActivityLog> {

	@Autowired
	private MemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Assignment assignment;

		masterId = super.getRequest().getData("masterId", int.class);
		assignment = this.repository.findAssignmentById(masterId);
		status = assignment != null;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		List<ActivityLog> activityLog;

		int masterId = super.getRequest().getData("masterId", int.class);
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		activityLog = this.repository.findByMemberIdAndAssignmentId(memberId, masterId);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		int masterId = super.getRequest().getData("masterId", int.class);
		Assignment assignment = this.repository.findAssignmentById(masterId);
		final boolean showCreate = assignment.getLeg().getStatus().equals(LegStatus.LANDED);

		dataset = super.unbindObject(activityLog, "registeredAt", "incidentType", "description", "severityLevel");

		super.addPayload(dataset, activityLog, "registeredAt", "incidentType");
		super.getResponse().addGlobal("showCreate", showCreate);
		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addData(dataset);

	}

}
