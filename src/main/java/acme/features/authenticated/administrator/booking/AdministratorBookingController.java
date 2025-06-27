
package acme.features.authenticated.administrator.booking;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.booking.Booking;

@GuiController
public class AdministratorBookingController extends AbstractGuiController<Administrator, Booking> {

	@Autowired
	private AdministratorBookingListService	administratorBookingListService;

	@Autowired
	private AdministratorBookingShowService	administratorBookingShowService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.administratorBookingListService);
		super.addBasicCommand("show", this.administratorBookingShowService);
	}
}
