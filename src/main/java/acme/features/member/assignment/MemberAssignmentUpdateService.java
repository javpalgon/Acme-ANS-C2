
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
public class MemberAssignmentUpdateService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int assignmentId;
		int memberId;
		Assignment assignment;

		assignmentId = super.getRequest().getData("id", int.class);
		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assignment = this.repository.findOneById(assignmentId);

		status = assignment.getIsDraftMode() && assignment.getMember().getId() == memberId && assignment.getMember().getAvailabilityStatus() == AvailabilityStatus.AVAILABLE;

		super.getResponse().setAuthorised(status);
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

		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		Integer legId = super.getRequest().getData("leg", int.class);

		super.bindObject(assignment, "role", "status", "remarks");

		if (legId != null)
			assignment.setLeg(this.repository.findLegById(legId));
		assignment.setMember(member);
	}

	@Override
	public void validate(final Assignment assignment) {
		assert assignment != null;

		Assignment original = this.repository.findOneById(assignment.getId());

		if (assignment.getRole() == Role.PILOT && !original.getRole().equals(assignment.getRole()))
			super.state(assignment.getLeg() == null || !this.repository.legHasPilot(assignment.getLeg().getId(), Role.PILOT, AssignmentStatus.CANCELLED), "role", "member.assignment.form.error.pilot-exists");

		if (assignment.getRole() == Role.CO_PILOT && !(original.getLeg().getId() == assignment.getLeg().getId()))
			super.state(assignment.getLeg() == null || !this.repository.legHasCoPilot(assignment.getLeg().getId(), Role.CO_PILOT, AssignmentStatus.CANCELLED), "role", "member.assignment.form.error.copilot-exists");

		if (assignment.getLeg() != null)
			super.state(!this.repository.hasLegOccurred(assignment.getLeg().getId(), MomentHelper.getCurrentMoment()), "leg", "member.assignment.form.error.leg-occurred");

		if (assignment.getLeg() != null)
			super.state(!assignment.getLeg().getFlight().getIsDraftMode(), "leg", "member.assignment.form.error.flight-not-published");

		if (assignment.getLeg() != null)
			super.state(!assignment.getLeg().getIsDraftMode(), "leg", "member.assignment.form.error.member-not-published");

		super.state(assignment.getMember() != null && assignment.getMember().getAvailabilityStatus() == AvailabilityStatus.AVAILABLE, "member", "member.assignment.form.error.member-unavailable");

		if (assignment.getMember() != null && assignment.getLeg() != null) {
			Integer memberId = assignment.getMember().getId();
			Integer legId = assignment.getLeg().getId();
			Integer assignmentId = (Integer) assignment.getId() != null ? assignment.getId() : 0;

			boolean duplicateAssignment = this.repository.existsByMemberAndLeg(memberId, legId, assignmentId, AssignmentStatus.CANCELLED);

			super.state(!duplicateAssignment, "leg", "member.assignment.form.error.duplicate-assignment");

			if (!duplicateAssignment && assignment.getStatus() != AssignmentStatus.CANCELLED) {
				boolean hasConflict = this.repository.hasScheduleConflict(memberId, assignment.getLeg().getDeparture(), assignment.getLeg().getArrival(), assignmentId, AssignmentStatus.CANCELLED);

				super.state(!hasConflict, "leg", "member.assignment.form.error.schedule-conflict");
			}
		}

	}

	@Override
	public void perform(final Assignment assignment) {
		assert assignment != null;

		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setMember(member);

		this.repository.save(assignment);
	}

	@Override
	public void unbind(final Assignment assignment) {
		assert assignment != null;

		Dataset dataset = super.unbindObject(assignment, "role", "lastUpdate", "status", "remarks", "isDraftMode");

		// Get current member
		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		// Prepare choices
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());
		SelectChoices roleChoices = SelectChoices.from(Role.class, assignment.getRole());

		// Get available legs
		List<Leg> legs = this.repository.findAllPublishedAndFutureLegs(MomentHelper.getCurrentMoment());
		SelectChoices legChoices = null;

		try {
			legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		} catch (NullPointerException e) {
		}
		dataset.put("role", roleChoices);
		dataset.put("status", statusChoices);
		dataset.put("legs", legChoices);

		// Handle selected leg
		String selectedLegKey = "";
		if (assignment.getLeg() != null) {

			boolean isLegAvailable = legs.stream().anyMatch(leg -> leg.getFlightNumber().equals(assignment.getLeg().getFlightNumber()));

			if (isLegAvailable)
				selectedLegKey = String.valueOf(assignment.getLeg().getId());
		}
		dataset.put("leg", selectedLegKey);

		// Add member info
		dataset.put("member", member.getEmployeeCode());

		super.getResponse().addData(dataset);
	}
}
