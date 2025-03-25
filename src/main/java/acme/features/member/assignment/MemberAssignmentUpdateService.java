
package acme.features.member.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.services.AbstractGuiService;
import acme.entities.assignment.Assignment;
import acme.realms.Member;

@Service
public class MemberAssignmentUpdateService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		// Solo los miembros con el rol "LEAD ATTENDANT" pueden actualizar asignaciones
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		// Cargar la asignación de vuelo existente
		int assignmentId = super.getRequest().getData("id", int.class);
		super.getBuffer().addData(this.repository.findOneById(assignmentId));
	}

	@Override
	public void perform(final Assignment assignment) {
		// Actualizar la asignación en la base de datos
		this.repository.save(assignment);
	}
}
