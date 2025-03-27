
package acme.features.customer.booking;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	private static final Logger			logger	= LoggerFactory.getLogger(CustomerBookingShowService.class);

	@Autowired
	protected CustomerBookingRepository	repository;


	@Override
	public void authorise() {
		Booking booking;
		int id;
		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		final int userAccountId = super.getRequest().getPrincipal().getAccountId();
		final int customerId = booking.getCustomer().getUserAccount().getId();
		super.getResponse().setAuthorised(userAccountId == customerId);
	}

	@Override
	public void load() {
		Booking booking;
		int id;
		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void unbind(final Booking object) {
		assert object != null;
		CustomerBookingShowService.logger.info("📦 Entrando en unbind() de Booking");

		Dataset dataset = super.unbindObject(object, "locatorCode", "purchaseMoment", "travelClass", "lastNibble", "isDraftMode");

		List<String> passengers = this.repository.findPassengersByBooking(object.getId()).stream().map(p -> p.getFullName()).toList();

		Money totalPrice = object.getPrice();
		dataset.put("totalPrice", totalPrice);
		CustomerBookingShowService.logger.info("💰 Precio total: {}", totalPrice);

		dataset.put("hasPassengers", !passengers.isEmpty());
		dataset.put("passengers", passengers);
		CustomerBookingShowService.logger.info("🧍 Pasajeros: {}", passengers);

		super.getResponse().addData(dataset);
	}

}
