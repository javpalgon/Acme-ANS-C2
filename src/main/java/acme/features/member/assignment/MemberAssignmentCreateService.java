
package acme.features.member.assignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Principal;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.assignment.Assignment;
import acme.entities.assignment.Role;
import acme.realms.Member;

@GuiService
public class MemberAssignmentCreateService extends AbstractGuiService<Member, Assignment> {

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

		// Verificar si el Member tiene el rol "LEAD_ATTENDANT"
		boolean isLeadAttendant = !this.repository.findByMemberAndRole(member, Role.LEAD_ATTENDANT).isEmpty();

		// Autorizar si el Member tiene el rol
		super.getResponse().setAuthorised(isLeadAttendant);
	}

	@Override
	public void load() {
		// Crear una nueva asignación de vuelo
		Assignment assignment = new Assignment();
		super.getBuffer().addData(assignment);
	}

	@Override
	public void perform(final Assignment assignment) {
		// Guardar la nueva asignación en la base de datos
		this.repository.save(assignment);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
