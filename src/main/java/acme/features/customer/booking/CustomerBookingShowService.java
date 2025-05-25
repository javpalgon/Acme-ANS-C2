
package acme.features.customer.booking;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.Travelclass;
import acme.realms.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	protected CustomerBookingRepository repository;


	@Override
	public void authorise() {
		Booking booking;
		int bookingId;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

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
		SelectChoices choices;
		choices = SelectChoices.from(Travelclass.class, object.getTravelClass());

		Dataset dataset = super.unbindObject(object, "locatorCode", "purchaseMoment", "travelClass", "lastNibble", "isDraftMode");

		String passengerList = this.repository.findPassengersByBooking(object.getId()).stream().map(p -> p.getFullName()).collect(Collectors.joining(", "));
		dataset.put("passengers", passengerList);
		dataset.put("hasPassengers", !passengerList.isEmpty());

		Money totalPrice = object.getPrice();
		dataset.put("totalPrice", totalPrice);
		dataset.put("travelClasses", choices);

		dataset.put("id", object.getId());

		super.getResponse().addData(dataset);
	}

}
