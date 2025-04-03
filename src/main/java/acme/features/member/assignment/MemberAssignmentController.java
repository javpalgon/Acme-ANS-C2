
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

	@Autowired
	private MemberAssignmentDeleteService			deleteService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {

		super.addCustomCommand("list-completed", "list", this.listCompletedService);
		super.addCustomCommand("list-planned", "list", this.listPlannedService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addBasicCommand("delete", this.deleteService);
	}

}
