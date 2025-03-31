
package acme.features.customer.booking;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		Booking booking;
		int id;
		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		final int userAccountId = super.getRequest().getPrincipal().getAccountId();
		final int customerId = booking.getCustomer().getUserAccount().getId();
		super.getResponse().setAuthorised(userAccountId == customerId && booking.getIsDraftMode());
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
	public void bind(final Booking object) {
		assert object != null;
		super.bindObject(object, "locatorCode", "purchaseMoment", "travelClass", "lastNibble");
	}

	@Override
	public void validate(final Booking object) {
		assert object != null;

		boolean hasNibble = object.getLastNibble() != null && !object.getLastNibble().isEmpty();
		super.state(hasNibble, "lastNibble", "customer.booking.form.error.last-nibble-required");
	}

	@Override
	public void perform(final Booking object) {
		assert object != null;
		object.setIsDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Booking object) {
		assert object != null;

		Dataset dataset = super.unbindObject(object, "locatorCode", "purchaseMoment", "travelClass", "lastNibble");

		List<String> passengers = this.repository.findPassengersByBooking(object.getId()).stream().map(p -> p.getFullName()).toList();

		Collection<Flight> flights = this.repository.findPublishedFlights();
		SelectChoices flightChoices = SelectChoices.from(flights, "tag", object.getFlight());

		Money totalPrice = object.getPrice();

		dataset.put("flights", flightChoices);
		dataset.put("totalPrice", totalPrice);
		dataset.put("passengers", passengers);

		super.getResponse().addData(dataset);
	}

}
