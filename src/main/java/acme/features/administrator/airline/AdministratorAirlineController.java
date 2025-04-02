
package acme.features.administrator.airline;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.entities.airline.Airline;

@Controller
public class AdministratorAirlineController extends AbstractGuiController<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineListService		listService;

	@Autowired
	private AdministratorAirlineShowService		showService;

	@Autowired
	private AdministratorAirlineCreateService	createService;

	@Autowired
	private AdministratorAirlineUpdateService	updateService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initials() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
	}
}
