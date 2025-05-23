
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
		boolean status = true;
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(memberId);

		status = member != null && member.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE;

		if (status)
			if (super.getRequest().hasData("leg", int.class)) {
				int legId = super.getRequest().getData("leg", int.class);
				if ((Integer) legId != null && legId != 0) {
					List<Leg> availableLegs = this.repository.findAllPFL(MomentHelper.getCurrentMoment(), member.getAirline().getId());
					boolean legIsValid = availableLegs.stream().anyMatch(l -> l.getId() == legId);
					if (!legIsValid)
						status = false;
				}
			}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Assignment assignment = new Assignment();
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setDraftMode(true);
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

		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		super.state(assignment.getMember() != null, "member", "member.assignment.form.error.member-null");

		if (assignment.getLeg() != null)
			super.state(!assignment.getLeg().getIsDraftMode(), "leg", "member.assignment.form.error.member-not-published");

		if (assignment.getLeg() != null)
			super.state(assignment.getMember().getAvailabilityStatus() == AvailabilityStatus.AVAILABLE, "member", "member.assignment.form.error.member-unavailable");

		if (assignment.getLeg() != null)
			super.state(!assignment.getLeg().getFlight().getIsDraftMode(), "leg", "member.assignment.form.error.flight-not-published");

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
		SelectChoices legChoices = SelectChoices.from(this.repository.findAllPFL(MomentHelper.getCurrentMoment(), member.getAirline().getId()), "flightNumber", assignment.getLeg());

		Dataset dataset = super.unbindObject(assignment, "lastUpdate", "status", "remarks", "draftMode");

		dataset.put("role", roleChoices);
		dataset.put("status", statusChoices);

		dataset.put("legs", legChoices);
		dataset.put("member", member.getEmployeeCode());

		super.getResponse().addData(dataset);
	}
}
