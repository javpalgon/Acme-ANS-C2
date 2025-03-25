
package acme.features.member.assignment;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.assignment.Assignment;
import acme.realms.Member;

@GuiService
public class MemberAssignmentListCompletedService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Member.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Assignment> assignments = new ArrayList<>();
		//Principal principal;
		int memberId;

		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assignments = this.repository.findAllAsignments();

		super.getBuffer().addData(assignments);
	}

	@Override
	public void unbind(final Assignment object) {

		assert object != null;

		Dataset dataset;

		// Desvincular los atributos de la entidad Assignment
		dataset = super.unbindObject(object, "role", "lastUpdate", "status", "remarks");

		// Agregar el Dataset a la respuesta
		super.getResponse().addData(dataset);
	}

}
