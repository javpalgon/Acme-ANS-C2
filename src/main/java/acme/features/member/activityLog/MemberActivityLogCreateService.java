
package acme.features.member.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.assignment.Assignment;
import acme.realms.Member;

@GuiService
public class MemberActivityLogCreateService extends AbstractGuiService<Member, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private MemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Assignment assignment;
		int memberId;

		masterId = super.getRequest().getData("masterId", int.class);
		assignment = this.repository.findAssignmentById(masterId);
		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		status = assignment != null && !assignment.getIsDraftMode() && assignment.getMember().getId() == memberId;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		ActivityLog activityLog;
		int masterId;
		Assignment assignment;

		masterId = super.getRequest().getData("masterId", int.class);
		assignment = this.repository.findAssignmentById(masterId);

		activityLog = new ActivityLog();
		activityLog.setAssignment(assignment);
		activityLog.setIsDraftMode(true);
		activityLog.setRegisteredAt(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(activityLog);

	}

	@Override
	public void bind(final ActivityLog activityLog) {

		super.bindObject(activityLog, "incidentType", "description", "severityLevel");

	}

	@Override
	public void validate(final ActivityLog activityLog) {
		assert activityLog != null;

		Assignment assignment = activityLog.getAssignment();
		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		super.state(activityLog.getSeverityLevel() != null && activityLog.getSeverityLevel() >= 0 && activityLog.getSeverityLevel() <= 10, "severityLevel", "member.activity-log.form.error.severity-range", 0, 10);

		super.state(assignment != null, "*", "member.activitylog.form.error.null-assignment");

		super.state(assignment != null && !assignment.getIsDraftMode(), "*", "member.activitylog.form.error.assignment-in-draft");

		super.state(assignment != null && assignment.getMember().getId() == currentMemberId, "*", "member.activitylog.form.error.assignment-not-owned");

		super.state(assignment.getLeg() != null && assignment.getLeg().getArrival().before(MomentHelper.getCurrentMoment()), "*", "member.activitylog.form.error.leg-not-completed");
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		assert activityLog != null;
		activityLog.setRegisteredAt(MomentHelper.getCurrentMoment());
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "incidentType", "registeredAt", "description", "severityLevel", "isDraftMode");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		//dataset.put("isDraftMode", activityLog.getAssignment().getIsDraftMode());
		dataset.put("registeredAt", MomentHelper.getCurrentMoment());

		super.getResponse().addData(dataset);
	}

}
