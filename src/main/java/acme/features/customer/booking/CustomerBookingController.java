
package acme.features.customer.booking;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@GuiController
public class CustomerBookingController extends AbstractGuiController<Customer, Booking> {

	@Autowired
	protected CustomerBookingListService	CustomerBookingListService;

	@Autowired
	protected CustomerBookingShowService	CustomerBookingShowService;

	@Autowired
	protected CustomerBookingUpdateService	CustomerBookingUpdateService;

	@Autowired
	protected CustomerBookingCreateService	CustomerBookingCreateService;

	@Autowired
	protected CustomerBookingPublishService	CustomerBookingPublishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.CustomerBookingListService);
		super.addBasicCommand("show", this.CustomerBookingShowService);
		super.addBasicCommand("update", this.CustomerBookingUpdateService);
		super.addBasicCommand("create", this.CustomerBookingCreateService);
		super.addCustomCommand("publish", "update", this.CustomerBookingPublishService);

	}

}
