
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

		status = assignment != null && assignment.getDraftMode() && assignment.getMember().getAvailabilityStatus() == AvailabilityStatus.AVAILABLE && assignment.getMember().getId() == memberId
			&& super.getRequest().getPrincipal().hasRealmOfType(Member.class);

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

		if ((Integer) assignment.getId() != null) {
			Assignment original = this.repository.findOneById(assignment.getId());

			if (original != null) {

				boolean roleChanged = !Objects.equals(assignment.getRole(), original.getRole());
				super.state(!roleChanged, "role", "member.assignment.form.error.readonly");

				boolean statusChanged = !Objects.equals(assignment.getStatus(), original.getStatus());
				super.state(!statusChanged, "status", "member.assignment.form.error.readonly");

				boolean legChanged = !Objects.equals(assignment.getLeg(), original.getLeg());
				super.state(!legChanged, "leg", "member.assignment.form.error.readonly");

				String remarks = assignment.getRemarks();
				String originalRemarks = original.getRemarks();
				if (remarks != null && remarks.trim().isEmpty())
					remarks = null;
				if (originalRemarks != null && originalRemarks.trim().isEmpty())
					originalRemarks = null;
				boolean remarksChanged = !Objects.equals(remarks, originalRemarks);
				super.state(!remarksChanged, "remarks", "member.assignment.form.error.readonly");
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
		SelectChoices legChoices;
		legChoices = SelectChoices.from(this.repository.findAllPFL(MomentHelper.getCurrentMoment(), member.getAirline().getId()), "flightNumber", assignment.getLeg());

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
