
package acme.features.member.assignment;

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
import acme.realms.Member;

@GuiService
public class MemberAssignmentUpdateService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Assignment assignment;

		masterId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findOneById(masterId);

		status = assignment.getIsDraftMode();

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Assignment assignment;
		int id;
		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findOneById(id);
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final Assignment assignment) {
		assert assignment != null;

		Integer legId = super.getRequest().getData("leg", int.class);
		Integer memberId = super.getRequest().getData("member", int.class);

		super.bindObject(assignment, "role", "lastUpdate", "status", "remarks");

		assignment.setLeg(this.repository.findLegById(legId));
		assignment.setMember(this.repository.findMemberById(memberId));
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
	}

	@Override
	public void validate(final Assignment assignment) {
		assert assignment != null;

		Assignment original = this.repository.findOneById(assignment.getId());

		if (!assignment.getIsDraftMode())
			super.state(assignment.getIsDraftMode(), "*", "member.assignment.form.error.notDraft", "isDraftMode");

		if (assignment.getRole() == Role.PILOT && !original.getRole().equals(assignment.getRole()))
			super.state(assignment.getLeg() == null || !this.repository.legHasPilot(assignment.getLeg().getId(), Role.PILOT, AssignmentStatus.CANCELLED), "role", "member.assignment.form.error.pilot-exists");

		if (assignment.getRole() == Role.CO_PILOT && !original.getRole().equals(assignment.getRole()))
			super.state(assignment.getLeg() == null || !this.repository.legHasCoPilot(assignment.getLeg().getId(), Role.CO_PILOT, AssignmentStatus.CANCELLED), "role", "member.assignment.form.error.copilot-exists");

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

	}

	@Override
	public void perform(final Assignment assignment) {
		assert assignment != null;
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final Assignment assignment) {
		assert assignment != null;

		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());
		SelectChoices roleChoices = SelectChoices.from(Role.class, assignment.getRole());
		SelectChoices legChoices = SelectChoices.from(this.repository.findAllLegs(), "flightNumber", assignment.getLeg());
		SelectChoices memberChoices = SelectChoices.from(this.repository.findAllMembers(), "employeeCode", assignment.getMember());

		Dataset dataset = super.unbindObject(assignment, "role", "lastUpdate", "status", "remarks", "isDraftMode");

		dataset.put("role", roleChoices);
		dataset.put("status", statusChoices);

		dataset.put("readonly", false);

		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("member", memberChoices.getSelected().getKey());
		dataset.put("members", memberChoices);

		super.getResponse().addData(dataset);
	}
}
