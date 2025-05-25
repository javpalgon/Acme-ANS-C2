
package acme.features.member.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.assignment.Assignment;
import acme.entities.assignment.AssignmentStatus;
import acme.realms.Member;

@GuiService
public class MemberActivityLogUpdateService extends AbstractGuiService<Member, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private MemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int activityLogId;
		Assignment assignment;
		ActivityLog activityLog;
		int memberId;

		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		activityLogId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findAssignmentByActivityLogId(activityLogId);
		activityLog = this.repository.findActivityLogById(activityLogId);

		status = activityLog.getDraftMode() && assignment != null && !assignment.getDraftMode() && assignment.getMember().getId() == memberId && !assignment.getStatus().equals(AssignmentStatus.CANCELLED);

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
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "registeredAt", "incidentType", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		assert activityLog != null;

		Assignment assignment = activityLog.getAssignment();
		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		super.state(activityLog.getSeverityLevel() != null && activityLog.getSeverityLevel() >= 0 && activityLog.getSeverityLevel() <= 10, "severityLevel", "member.activity-log.form.error.severity-range");

		super.state(assignment != null, "*", "member.activitylog.form.error.null-assignment");

		super.state(assignment != null && !assignment.getDraftMode(), "*", "member.activitylog.form.error.assignment-in-draft");

		super.state(assignment.getLeg() != null && assignment.getLeg().getArrival().before(MomentHelper.getCurrentMoment()), "*", "member.activitylog.form.error.leg-not-completed");

		super.state(assignment != null && assignment.getMember().getId() == currentMemberId, "*", "member.activitylog.form.error.assignment-not-owned");

	}

	@Override
	public void perform(final ActivityLog activityLog) {
		int actividityLogId = activityLog.getId();
		ActivityLog original = this.repository.findActivityLogById(actividityLogId);
		activityLog.setRegisteredAt(original.getRegisteredAt());

		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registeredAt", "incidentType", "description", "severityLevel", "draftMode");

		dataset.put("masterId", activityLog.getAssignment().getId());
		//dataset.put("isDraftMode", activityLog.getAssignment().getIsDraftMode());

		super.getResponse().addData(dataset);
	}

}
