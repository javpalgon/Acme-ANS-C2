
package acme.features.member.assignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.assignment.Assignment;
import acme.entities.leg.LegStatus;
import acme.realms.Member;

@GuiService
public class MemberAssignmentListPlannedService extends AbstractGuiService<Member, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Member.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int memberId;

		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		super.getBuffer().addData(this.repository.findByLegStatusNotAndMemberId(LegStatus.LANDED, memberId));
	}

	@Override
	public void unbind(final Assignment assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "role", "lastUpdate", "status", "remarks");

		dataset.put("leg.flightNumber", assignment.getLeg().getFlightNumber());

		// Agregar el Dataset a la respuesta
		super.getResponse().addData(dataset);
	}
}
