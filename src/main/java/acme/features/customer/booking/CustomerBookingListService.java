
package acme.features.customer.booking;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@GuiService
public class CustomerBookingListService extends AbstractGuiService<Customer, Booking> {

	private static final Logger			logger	= LoggerFactory.getLogger(CustomerBookingListService.class);

	@Autowired
	private CustomerBookingRepository	repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);

	}

	@Override
	public void load() {
		Collection<Booking> bookings = new ArrayList<>();
		int customerId;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		bookings = this.repository.findBookingsByCustomerId(customerId);
		super.getBuffer().addData(bookings);
	}

	@Override
	public void unbind(final Booking object) {
		Dataset dataset;
		dataset = super.unbindObject(object, "locatorCode", "purchaseMoment", "travelClass", "lastNibble", "isDraftMode");
		CustomerBookingListService.logger.info("Precio total: {}", object.getPrice());
		dataset.put("totalPrice", object.getPrice());
		dataset.put("flight", object.getFlight().getTag());
		super.getResponse().addData(dataset);
	}

}
