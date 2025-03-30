
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
import acme.entities.leg.Leg;
import acme.realms.Member;

@GuiService
public class MemberAssignmentCreateService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		//Member member = this.repository.findMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());
		//boolean isLeadAttendant = member != null && !this.repository.findByMemberAndRole(member, Role.LEAD_ATTENDANT).isEmpty();
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Member.class));
	}

	@Override
	public void load() {
		Assignment assignment = new Assignment();
		assignment.setIsDraftMode(true);
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final Assignment assignment) {
		assert assignment != null;

		super.bindObject(assignment, "role", "lastUpdate", "status", "remarks", "isDraftMode", "member");
		Integer legId = super.getRequest().getData("leg", Integer.class);
		Leg leg = legId != null ? this.repository.findLegById(legId) : null;
		assignment.setLeg(leg);

	}

	@Override
	public void validate(final Assignment assignment) {
		assert assignment != null;

		super.state(assignment.getLastUpdate() == null || !assignment.getLastUpdate().after(MomentHelper.getCurrentMoment()), "lastUpdate", "member.assignment.form.error.lastUpdate-future");

		super.state(assignment.getLeg() == null || !this.repository.hasLegOccurred(assignment.getLeg().getId(), MomentHelper.getCurrentMoment()), "leg", "member.assignment.form.error.leg-occurred");

		super.state(assignment.getMember() != null && assignment.getMember().getAvailabilityStatus() == AvailabilityStatus.AVAILABLE, "member", "member.assignment.form.error.member-unavailable");

		if (assignment.getMember() != null && assignment.getLeg() != null) {
			Integer memberId = assignment.getMember().getId();
			Integer legId = assignment.getLeg().getId();
			Integer assignmentId = (Integer) assignment.getId() != null ? assignment.getId() : 0;

			boolean duplicateAssignment = this.repository.existsByMemberAndLeg(memberId, legId, assignmentId);

			super.state(!duplicateAssignment, "member", "member.assignment.form.error.duplicate-assignment");

			if (!duplicateAssignment && assignment.getStatus() != AssignmentStatus.CANCELLED) {
				boolean hasConflict = this.repository.hasScheduleConflict(memberId, assignment.getLeg().getDeparture(), assignment.getLeg().getArrival(), assignmentId, AssignmentStatus.CANCELLED);

				super.state(!hasConflict, "member", "member.assignment.form.error.schedule-conflict");
			}
		}

		if (assignment.getRole() == Role.PILOT)
			super.state(assignment.getLeg() == null || !this.repository.legHasPilot(assignment.getLeg().getId(), Role.PILOT), "role", "member.assignment.form.error.pilot-exists");

		if (assignment.getRole() == Role.CO_PILOT)
			super.state(assignment.getLeg() == null || !this.repository.legHasCoPilot(assignment.getLeg().getId(), Role.CO_PILOT), "role", "member.assignment.form.error.copilot-exists");
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

		Dataset dataset = super.unbindObject(assignment, "role", "lastUpdate", "status", "remarks", "isDraftMode");

		dataset.put("role", SelectChoices.from(Role.class, assignment.getRole()));
		dataset.put("status", SelectChoices.from(AssignmentStatus.class, assignment.getStatus()));
		dataset.put("legs", SelectChoices.from(this.repository.findAllLegs(), "flightNumber", assignment.getLeg()));
		dataset.put("members", SelectChoices.from(this.repository.findAllMembers(), "employeeCode", assignment.getMember()));

		super.getResponse().addData(dataset);
	}
}
