
package acme.features.member.assignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.assignment.Assignment;
import acme.realms.Member;

@GuiService
public class MemberAssignmentShowService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		// Solo los miembros de la tripulación pueden ver detalles de una asignación
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Member.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		// Cargar la asignación de vuelo solicitada
		int assignmentId = super.getRequest().getData("id", int.class);
		super.getBuffer().addData(this.repository.findOneById(assignmentId));
	}

	@Override
	public void unbind(final Assignment assignment) {
		Dataset dataset;

		// Desvincular los atributos de la entidad Assignment
		dataset = super.unbindObject(assignment, "role", "lastUpdate", "status", "remarks");

		// Desvincular atributos relacionados (leg y member)
		dataset.put("leg", assignment.getLeg());
		dataset.put("member", assignment.getMember());

		// Agregar el Dataset a la respuesta
		super.getResponse().addData(dataset);
	}
}
