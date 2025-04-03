
package acme.features.member.assignment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.assignment.Assignment;
import acme.features.member.activityLog.MemberActivityLogRepository;
import acme.realms.Member;

@GuiService
public class MemberAssignmentDeleteService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository	repository;

	@Autowired
	private MemberActivityLogRepository	ALrepository;


	@Override
	public void authorise() {
		boolean status;
		int assignmentId;
		Assignment assignment;
		int memberId;

		assignmentId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findOneById(assignmentId);
		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		status = assignment != null && assignment.getIsDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(Member.class) && assignment.getMember().getId() == memberId;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Assignment assignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findOneById(id);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final Assignment assignment) {
		assert assignment != null;

		super.bindObject(assignment, "role", "status", "remarks", "leg", "member");
	}

	@Override
	public void validate(final Assignment assignment) {
		assert assignment != null;

		if (!assignment.getIsDraftMode())
			super.state(false, "*", "member.assignment.form.error.notDraft");

		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		List<ActivityLog> activityLogs = this.ALrepository.findByMemberIdAndAssignmentId(memberId, assignment.getId());
		boolean allDraft = activityLogs.stream().allMatch(ActivityLog::getIsDraftMode);

		if (!allDraft)
			super.state(false, "*", "member.assignment.form.error.activityLogsNotDraft");
	}

	@Override
	public void perform(final Assignment assignment) {
		assert assignment != null;

		this.repository.deleteActivityLogsByAssignmentId(assignment.getId());

		this.repository.delete(assignment);

	}

	@Override
	public void unbind(final Assignment assignment) {
		assert assignment != null;

		Dataset dataset;
		dataset = super.unbindObject(assignment, "role", "status", "remarks", "isDraftMode", "leg", "member");

		super.getResponse().addData(dataset);
	}

}
