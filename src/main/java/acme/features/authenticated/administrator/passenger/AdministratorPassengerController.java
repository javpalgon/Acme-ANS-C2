
package acme.features.authenticated.administrator.passenger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.passenger.Passenger;

@GuiController
public class AdministratorPassengerController extends AbstractGuiController<Administrator, Passenger> {

	@Autowired
	protected AdministratorPassengerListService	administratorPassengerListService;

	@Autowired
	protected AdministratorPassengerShowService	administratorPassengerShowService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.administratorPassengerListService);
		super.addBasicCommand("show", this.administratorPassengerShowService);

	}

}
