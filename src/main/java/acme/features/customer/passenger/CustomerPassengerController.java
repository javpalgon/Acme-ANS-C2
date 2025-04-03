
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
	protected CustomerPassengerListService		customerPassengerListService;

	@Autowired
	protected CustomerPassengerShowService		customerPassengerShowService;

	@Autowired
	protected CustomerPassengerUpdateService	customerPassengerUpdateService;

	@Autowired
	protected CustomerPassengerCreateService	customerPassengerCreateService;

	@Autowired
	protected CustomerPassengerPublishService	customerPassengerPublishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.customerPassengerListService);
		super.addBasicCommand("show", this.customerPassengerShowService);
		super.addBasicCommand("update", this.customerPassengerUpdateService);
		super.addBasicCommand("create", this.customerPassengerCreateService);
		super.addCustomCommand("publish", "update", this.customerPassengerPublishService);

	}

}
