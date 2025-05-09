
package acme.features.assistanceAgent.trackingLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.trackinglog.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiController
public class AgentTrackingLogController extends AbstractGuiController<AssistanceAgent, TrackingLog> {

	@Autowired
	private AgentTrackingLogListService		listService;

	@Autowired
	private AgentTrackingLogShowService		showService;

	@Autowired
	private AgentTrackingLogCreateService	createService;

	@Autowired
	private AgentTrackingLogDeleteService	deleteService;

	@Autowired
	private AgentTrackingLogUpdateService	updateService;

	@Autowired
	private AgentTrackingLogPublishService	publishService;

	// Constructors -------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
