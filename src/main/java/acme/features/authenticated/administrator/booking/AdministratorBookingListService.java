
package acme.features.authenticated.administrator.booking;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;

@GuiService
public class AdministratorBookingListService extends AbstractGuiService<Administrator, Booking> {

	@Autowired
	private AdministratorBookingRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Booking> bookings = new ArrayList<>();

		bookings = this.repository.findPublishedBookings();
		super.getBuffer().addData(bookings);
	}

	@Override
	public void unbind(final Booking object) {
		Dataset dataset;
		dataset = super.unbindObject(object, "locatorCode", "purchaseMoment", "travelClass", "lastNibble", "isDraftMode", "customer.identifier");
		dataset.put("totalPrice", object.getPrice());
		dataset.put("flight", object.getFlight().getTag());
		super.getResponse().addData(dataset);
	}

}
