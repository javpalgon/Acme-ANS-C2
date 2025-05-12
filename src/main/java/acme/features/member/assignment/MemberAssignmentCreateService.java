
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
public class MemberAssignmentCreateService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Member.class) && member.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Assignment assignment = new Assignment();
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setIsDraftMode(true);
		assignment.setStatus(AssignmentStatus.PENDING);
		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		assignment.setMember(member);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final Assignment assignment) {
		assert assignment != null;

		super.bindObject(assignment, "role", "remarks", "leg");
	}

	@Override
	public void validate(final Assignment assignment) {
		assert assignment != null;

		super.state(assignment.getMember() != null, "member", "member.assignment.form.error.member-null");

		super.state(assignment.getLeg() != null, "leg", "member.assignment.form.error.leg-null");

		if (assignment.getLeg() != null)
			super.state(!assignment.getLeg().getIsDraftMode(), "leg", "member.assignment.form.error.member-not-published");

		if (assignment.getMember() != null)
			super.state(assignment.getMember().getAvailabilityStatus() == AvailabilityStatus.AVAILABLE, "member", "member.assignment.form.error.member-unavailable");

		if (assignment.getLeg() != null)
			super.state(!this.repository.hasLegOccurred(assignment.getLeg().getId(), MomentHelper.getCurrentMoment()), "leg", "member.assignment.form.error.leg-occurred");

		if (assignment.getMember() != null && assignment.getLeg() != null) {
			Integer memberId = assignment.getMember().getId();
			Integer legId = assignment.getLeg().getId();
			Integer assignmentId = assignment.getId();

			boolean duplicateAssignment = this.repository.existsByMemberAndLeg(memberId, legId, assignmentId, AssignmentStatus.CANCELLED);

			super.state(!duplicateAssignment, "member", "member.assignment.form.error.duplicate-assignment");

			if (!duplicateAssignment && assignment.getStatus() != AssignmentStatus.CANCELLED) {
				boolean hasConflict = this.repository.hasScheduleConflict(memberId, assignment.getLeg().getDeparture(), assignment.getLeg().getArrival(), assignmentId, AssignmentStatus.CANCELLED);

				super.state(!hasConflict, "leg", "member.assignment.form.error.schedule-conflict");
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
		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		assignment.setMember(member);

		this.repository.save(assignment);
	}

	@Override
	public void unbind(final Assignment assignment) {
		assert assignment != null;

		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());
		SelectChoices roleChoices = SelectChoices.from(Role.class, assignment.getRole());
		SelectChoices legChoices = SelectChoices.from(this.repository.findAllPublishedAndFutureLegs(MomentHelper.getCurrentMoment()), "flightNumber", null);

		Dataset dataset = super.unbindObject(assignment, "role", "lastUpdate", "status", "remarks", "isDraftMode");

		dataset.put("role", roleChoices);
		dataset.put("status", statusChoices);

		//dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("member", member.getEmployeeCode());

		super.getResponse().addData(dataset);
	}
}
