
package acme.features.administrator.airport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.airport.Airport;

@GuiController
public class AdministratorAirportController extends AbstractGuiController<Administrator, Airport> {

	@Autowired
	protected AdministratorAirportListService	administratorAirportListService;

	@Autowired
	protected AdministratorAirportShowService	administratorAirportShowService;

	@Autowired
	protected AdministratorAirportCreateService	administratorAirportCreateService;

	@Autowired
	protected AdministratorAirportUpdateService	administratorAirportUpdateService;


	@PostConstruct
	protected void initials() {
		super.addBasicCommand("list", this.administratorAirportListService);
		super.addBasicCommand("show", this.administratorAirportShowService);
		super.addBasicCommand("create", this.administratorAirportCreateService);
		super.addBasicCommand("update", this.administratorAirportUpdateService);

	}
}
