
package acme.features.customer.passenger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiController
public class CustomerPassengerController extends AbstractGuiController<Customer, Passenger> {

	@Autowired
	protected CustomerPassengerListService	customerPassengerListService;

	@Autowired
	protected CustomerPassengerShowService	customerPassengerShowService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.customerPassengerListService);
		super.addBasicCommand("show", this.customerPassengerShowService);
	}

}
