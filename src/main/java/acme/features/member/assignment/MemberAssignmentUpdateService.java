
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
		boolean status = true;

		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(memberId);
		int masterId = super.getRequest().getData("id", int.class);
		Assignment assignment = this.repository.findOneById(masterId);

		status = member != null && assignment != null && super.getRequest().getPrincipal().hasRealmOfType(Member.class) && assignment.getMember().getId() == memberId && member.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE;

		if (status && super.getRequest().getMethod().equals("POST")) {

			if (super.getRequest().hasData("leg", int.class)) {
				int legId = super.getRequest().getData("leg", int.class);
				if ((Integer) legId != null && legId != 0) {
					List<Leg> availableLegs = this.repository.findAllPFL(MomentHelper.getCurrentMoment(), member.getAirline().getId());
					boolean legIsValid = availableLegs.stream().anyMatch(l -> l.getId() == legId);
					if (!legIsValid)
						status = false;
				}
			}

			if (status && super.getRequest().hasData("status")) {
				String currentStatus = super.getRequest().getData("status", String.class);
				status = this.isValidAssignmentStatus(currentStatus);
			}

			if (status && super.getRequest().hasData("role")) {
				String currentRole = super.getRequest().getData("role", String.class);
				status = this.isValidRole(currentRole);
			}
		}

		super.getResponse().setAuthorised(status);
	}

	private boolean isValidAssignmentStatus(final String status) {
		return status.equals("0") || status.equals("CONFIRMED") || status.equals("PENDING") || status.equals("CANCELLED");
	}

	private boolean isValidRole(final String role) {
		return role.equals("0") || role.equals("PILOT") || role.equals("CO_PILOT") || role.equals("LEAD_ATTENDANT") || role.equals("CABIN_ATTENDANT");
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

		super.bindObject(assignment, "role", "status", "remarks", "leg");
	}

	@Override
	public void validate(final Assignment assignment) {
		assert assignment != null;

		Assignment original = this.repository.findOneById(assignment.getId());

		if (assignment.getLeg() != null) {
			if (assignment.getRole() == Role.CO_PILOT && (!original.getRole().equals(assignment.getRole()) || original.getLeg().getId() != assignment.getLeg().getId()))
				super.state(!this.repository.legHasCoPilot(assignment.getLeg().getId(), Role.CO_PILOT, AssignmentStatus.CANCELLED), "role", "member.assignment.form.error.copilot-exists");

			if (assignment.getRole() == Role.PILOT && (!original.getRole().equals(assignment.getRole()) || original.getLeg().getId() != assignment.getLeg().getId()))
				super.state(!this.repository.legHasPilot(assignment.getLeg().getId(), Role.PILOT, AssignmentStatus.CANCELLED), "role", "member.assignment.form.error.pilot-exists");
		}

		boolean legChanged = false;
		if (assignment.getLeg() != null)
			legChanged = !(original.getLeg().getId() == assignment.getLeg().getId());

		if (legChanged && assignment.getLeg() != null) {
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
