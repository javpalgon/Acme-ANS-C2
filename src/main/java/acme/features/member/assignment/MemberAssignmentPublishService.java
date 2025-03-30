
package acme.features.member.assignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Principal;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.assignment.Assignment;
import acme.entities.assignment.AssignmentStatus;
import acme.realms.Member;

@GuiService
public class MemberAssignmentPublishService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		// Obtener el Principal (usuario autenticado)
		Principal principal = super.getRequest().getPrincipal();

		// Obtener el ID del Member actual (usuario autenticado)
		int memberId = principal.getAccountId();

		// Obtener el Member actual desde el repositorio
		Member member = this.repository.findMemberById(memberId);

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int assignmentId = super.getRequest().getData("id", int.class);
		super.getBuffer().addData(this.repository.findOneById(assignmentId));
	}

	@Override
	public void perform(final Assignment assignment) {
		assignment.setStatus(AssignmentStatus.CONFIRMED);
		this.repository.save(assignment);
	}
}
