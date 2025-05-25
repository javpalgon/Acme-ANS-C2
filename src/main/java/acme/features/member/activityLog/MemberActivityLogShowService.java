
package acme.features.member.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.assignment.Assignment;
import acme.entities.assignment.AssignmentStatus;
import acme.realms.Member;

@GuiService
public class MemberActivityLogShowService extends AbstractGuiService<Member, ActivityLog> {

	@Autowired
	private MemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int activityLogId;
		Assignment assignment;
		int memberId;

		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		activityLogId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findAssignmentByActivityLogId(activityLogId);

		status = assignment != null && !assignment.getDraftMode() && assignment.getMember().getId() == memberId && !assignment.getStatus().equals(AssignmentStatus.CANCELLED);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registeredAt", "incidentType", "description", "severityLevel", "draftMode");
		dataset.put("masterId", activityLog.getAssignment().getId());
		dataset.put("masterIsDraftMode", activityLog.getAssignment().getDraftMode());

		super.getResponse().addData(dataset);
	}

}
