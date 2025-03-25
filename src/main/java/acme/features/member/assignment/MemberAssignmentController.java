
package acme.features.member.assignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.assignment.Assignment;
import acme.realms.Member;

@GuiController
public class MemberAssignmentController extends AbstractGuiController<Member, Assignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private MemberAssignmentListCompletedService	listCompletedService;

	@Autowired
	private MemberAssignmentListPlannedService		listPlannedService;

	@Autowired
	private MemberAssignmentShowService				showService;

	@Autowired
	private MemberAssignmentCreateService			createService;

	@Autowired
	private MemberAssignmentUpdateService			updateService;

	@Autowired
	private MemberAssignmentPublishService			publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		assert this.listCompletedService != null : "listCompletedService no debe ser nulo";
		assert this.listPlannedService != null : "listPlannedService no debe ser nulo";

		super.addCustomCommand("completed", "list", this.listCompletedService);
		super.addCustomCommand("planned", "list", this.listPlannedService);
		super.addBasicCommand("show", this.showService);
		//super.addBasicCommand("create", this.createService);
		//super.addBasicCommand("update", this.updateService);
		//super.addBasicCommand("publish", this.publishService);
	}

}
