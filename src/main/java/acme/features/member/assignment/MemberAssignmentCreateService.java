
package acme.features.member.assignment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.assignment.Assignment;
import acme.entities.assignment.AssignmentStatus;
import acme.entities.assignment.Role;
import acme.entities.flightcrewmember.AvailabilityStatus;
import acme.entities.leg.Leg;
import acme.realms.Member;

@GuiService
public class MemberAssignmentCreateService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Member.class));
	}

	@Override
	public void load() {
		Assignment assignment = new Assignment();
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setIsDraftMode(true);
		assignment.setStatus(AssignmentStatus.PENDING);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final Assignment assignment) {
		assert assignment != null;

		super.bindObject(assignment, "role", "remarks", "leg", "member");
	}

	@Override
	public void validate(final Assignment assignment) {
		assert assignment != null;

		super.state(assignment.getLeg() == null || !this.repository.hasLegOccurred(assignment.getLeg().getId(), MomentHelper.getCurrentMoment()), "leg", "member.assignment.form.error.leg-occurred");

		super.state(assignment.getMember() != null && assignment.getMember().getAvailabilityStatus() == AvailabilityStatus.AVAILABLE, "member", "member.assignment.form.error.member-unavailable");

		if (assignment.getMember() != null && assignment.getLeg() != null) {
			Integer memberId = assignment.getMember().getId();
			Integer legId = assignment.getLeg().getId();
			Integer assignmentId = (Integer) assignment.getId() != null ? assignment.getId() : 0;

			boolean duplicateAssignment = this.repository.existsByMemberAndLeg(memberId, legId, assignmentId, AssignmentStatus.CANCELLED);

			super.state(!duplicateAssignment, "member", "member.assignment.form.error.duplicate-assignment");

			if (!duplicateAssignment && assignment.getStatus() != AssignmentStatus.CANCELLED) {
				boolean hasConflict = this.repository.hasScheduleConflict(memberId, assignment.getLeg().getDeparture(), assignment.getLeg().getArrival(), assignmentId, AssignmentStatus.CANCELLED);

				super.state(!hasConflict, "member", "member.assignment.form.error.schedule-conflict");
			}
		}

		if (assignment.getRole() == Role.PILOT)
			super.state(assignment.getLeg() == null || !this.repository.legHasPilot(assignment.getLeg().getId(), Role.PILOT, AssignmentStatus.CANCELLED), "role", "member.assignment.form.error.pilot-exists");

		if (assignment.getRole() == Role.CO_PILOT)
			super.state(assignment.getLeg() == null || !this.repository.legHasCoPilot(assignment.getLeg().getId(), Role.CO_PILOT, AssignmentStatus.CANCELLED), "role", "member.assignment.form.error.copilot-exists");
	}

	@Override
	public void perform(final Assignment assignment) {
		assert assignment != null;

		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setStatus(AssignmentStatus.PENDING);

		this.repository.save(assignment);
	}

	@Override
	public void unbind(final Assignment assignment) {
		assert assignment != null;

		Dataset dataset = super.unbindObject(assignment, "role", "lastUpdate", "remarks", "isDraftMode");

		List<Member> availableMembers = this.repository.findAvailableMembers(AvailabilityStatus.AVAILABLE);

		SelectChoices membersChoices = SelectChoices.from(availableMembers, "employeeCode", assignment.getMember());

		dataset.put("members", membersChoices);

		dataset.put("role", SelectChoices.from(Role.class, assignment.getRole()));

		List<Leg> validLegs = this.repository.findAllPublishedAndFutureLegs(MomentHelper.getCurrentMoment());
		dataset.put("legs", SelectChoices.from(validLegs, "flightNumber", assignment.getLeg()));
		super.getResponse().addData(dataset);
	}
}
