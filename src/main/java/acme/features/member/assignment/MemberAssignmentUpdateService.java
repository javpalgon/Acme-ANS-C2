
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
		boolean status = false;

		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(memberId);

		if (super.getRequest().hasData("id", int.class)) {
			int assignmentId = super.getRequest().getData("id", int.class);
			Assignment assignment = this.repository.findOneById(assignmentId);

			if (assignment != null && member != null && assignment.getMember() != null && assignment.getLeg() != null && assignment.getLeg().getAircraft() != null && assignment.getLeg().getAircraft().getAirline() != null && member.getAirline() != null)
				status = member.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE && memberId == assignment.getMember().getId() && assignment.getLeg().getAircraft().getAirline().getId() == member.getAirline().getId();

			if (status && super.getRequest().hasData("leg", int.class)) {
				Integer legId = super.getRequest().getData("leg", int.class);
				if (legId != null && legId != 0) {
					List<Leg> availableLegs = this.repository.findAllPFL(MomentHelper.getCurrentMoment(), member.getAirline().getId());
					boolean legIsValid = availableLegs.stream().anyMatch(l -> l.getId() == legId);
					if (!legIsValid)
						status = false;
				}
			}
		}

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

		super.bindObject(assignment, "role", "status", "remarks", "leg");
	}

	@Override
	public void validate(final Assignment assignment) {
		assert assignment != null;

		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		Assignment original = this.repository.findOneById(assignment.getId());

		if (assignment.getRole() == Role.PILOT && !original.getRole().equals(assignment.getRole()))
			super.state(assignment.getLeg() == null || !this.repository.legHasPilot(assignment.getLeg().getId(), Role.PILOT, AssignmentStatus.CANCELLED), "role", "member.assignment.form.error.pilot-exists");

		if (assignment.getRole() == Role.CO_PILOT && !(original.getLeg().getId() == assignment.getLeg().getId()))
			super.state(assignment.getLeg() == null || !this.repository.legHasCoPilot(assignment.getLeg().getId(), Role.CO_PILOT, AssignmentStatus.CANCELLED), "role", "member.assignment.form.error.copilot-exists");

		if (assignment.getLeg() != null)
			super.state(!assignment.getLeg().getFlight().getIsDraftMode(), "leg", "member.assignment.form.error.flight-not-published");

		if (assignment.getLeg() != null)
			super.state(!assignment.getLeg().getIsDraftMode(), "leg", "member.assignment.form.error.member-not-published");

		super.state(assignment.getMember() != null && assignment.getMember().getAvailabilityStatus() == AvailabilityStatus.AVAILABLE, "member", "member.assignment.form.error.member-unavailable");

		boolean memberChanged = !(original.getMember().getId() == assignment.getMember().getId());

		boolean legChanged = false;
		if (original.getLeg() == null && assignment.getLeg() != null)
			legChanged = true;
		else if (original.getLeg() != null && assignment.getLeg() == null)
			legChanged = true;
		else if (original.getLeg() != null && assignment.getLeg() != null)
			legChanged = !(original.getLeg().getId() == assignment.getLeg().getId());

		if ((memberChanged || legChanged) && assignment.getMember() != null && assignment.getLeg() != null) {
			Integer memberId = assignment.getMember().getId();
			Integer legId = assignment.getLeg().getId();
			Integer assignmentId = assignment.getId();

			boolean duplicateAssignment = this.repository.existsByMemberAndLeg(memberId, legId, assignmentId, AssignmentStatus.CANCELLED);

			super.state(!duplicateAssignment, "leg", "member.assignment.form.error.duplicate-assignment");

			if (!duplicateAssignment && assignment.getStatus() != AssignmentStatus.CANCELLED) {
				boolean hasConflict = this.repository.hasScheduleConflict(memberId, assignment.getLeg().getDeparture(), assignment.getLeg().getArrival(), assignmentId, AssignmentStatus.CANCELLED);
				super.state(!hasConflict, "leg", "member.assignment.form.error.schedule-conflict");
			}
		}

		if (assignment.getLeg() != null)
			super.state(!this.repository.hasLegOccurred(assignment.getLeg().getId(), MomentHelper.getCurrentMoment()), "leg", "member.assignment.form.error.leg-occurred");

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

		Dataset dataset = super.unbindObject(assignment, "role", "lastUpdate", "status", "remarks", "draftMode");

		// Get current member
		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		// Prepare choices
		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());
		SelectChoices roleChoices = SelectChoices.from(Role.class, assignment.getRole());

		// Get available legs
		List<Leg> legs = this.repository.findAllPFL(MomentHelper.getCurrentMoment(), member.getAirline().getId());
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
