
package acme.features.member.assignment;

import java.util.Objects;

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
public class MemberAssignmentPublishService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Assignment assignment;

		masterId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findOneById(masterId);
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		status = assignment.getDraftMode() && assignment.getMember().getId() == memberId && assignment.getMember().getAvailabilityStatus() == AvailabilityStatus.AVAILABLE;

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

		Integer legId = super.getRequest().getData("leg", int.class);

		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		assignment.setLeg(this.repository.findLegById(legId));
		assignment.setMember(member);
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());

		super.bindObject(assignment, "role", "status", "remarks");
	}

	@Override
	public void validate(final Assignment assignment) {
		assert assignment != null;

		if (assignment.getLeg() != null)
			super.state(!this.repository.hasLegOccurred(assignment.getLeg().getId(), MomentHelper.getCurrentMoment()), "leg", "member.assignment.form.error.leg-occurred");

		if ((Integer) assignment.getId() != null) {
			Assignment original = this.repository.findOneById(assignment.getId());

			if (original != null) {
				boolean hasChanged = false;
				if (assignment.getRole() != null) {
					if (!assignment.getRole().equals(original.getRole()))
						hasChanged = true;
				} else if (original.getRole() != null)
					hasChanged = true;
				super.state(!hasChanged, "role", "member.assignment.form.error.readonly");

				if (assignment.getStatus() != null) {
					if (!assignment.getStatus().equals(original.getStatus()))
						hasChanged = true;
				} else if (original.getStatus() != null)
					hasChanged = true;
				super.state(!hasChanged, "status", "member.assignment.form.error.readonly");

				if (assignment.getLeg() != null) {
					if (!assignment.getLeg().equals(original.getLeg()))
						hasChanged = true;
				} else if (original.getLeg() != null)
					hasChanged = true;
				super.state(!hasChanged, "leg", "member.assignment.form.error.readonly");

				String remarks = assignment.getRemarks() == null || assignment.getRemarks().isEmpty() ? null : assignment.getRemarks();
				String originalRemarks = original.getRemarks() == null || original.getRemarks().isEmpty() ? null : original.getRemarks();
				hasChanged = !Objects.equals(remarks, originalRemarks);
				super.state(!hasChanged, "remarks", "member.assignment.form.error.readonly");
			}
		}
	}

	@Override
	public void perform(final Assignment assignment) {
		assert assignment != null;

		assignment.setDraftMode(false);
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setStatus(AssignmentStatus.CONFIRMED);

		this.repository.save(assignment);
	}

	@Override
	public void unbind(final Assignment assignment) {
		assert assignment != null;

		int currentMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Member member = this.repository.findMemberById(currentMemberId);

		SelectChoices statusChoices = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());
		SelectChoices roleChoices = SelectChoices.from(Role.class, assignment.getRole());
		SelectChoices legChoices = assignment.getDraftMode() ? SelectChoices.from(this.repository.findAllPFL(MomentHelper.getCurrentMoment(), member.getAirline().getId()), "flightNumber", assignment.getLeg())
			: SelectChoices.from(this.repository.findAllLegs(), "flightNumber", assignment.getLeg());

		Dataset dataset = super.unbindObject(assignment, "role", "lastUpdate", "status", "remarks", "draftMode");

		dataset.put("role", roleChoices);
		dataset.put("status", statusChoices);

		dataset.put("readonly", false);

		String selectedLeg = legChoices.getSelected().getKey();

		dataset.put("leg", selectedLeg);
		dataset.put("legs", legChoices);
		dataset.put("member", member.getEmployeeCode());

		super.getResponse().addData(dataset);
	}
}
