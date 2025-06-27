
package acme.features.authenticated.administrator.booking;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;

@GuiService
public class AdministratorBookingShowService extends AbstractGuiService<Administrator, Booking> {

	@Autowired
	private AdministratorBookingRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(id);
		boolean authorised = booking != null && !booking.getIsDraftMode();

		super.getResponse().setAuthorised(authorised);
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

		Dataset dataset = super.unbindObject(object, "locatorCode", "purchaseMoment", "travelClass", "lastNibble", "isDraftMode", "customer.identifier");

		String passengerList = this.repository.findPassengersByBooking(object.getId()).stream().map(p -> p.getFullName()).collect(Collectors.joining(", "));
		dataset.put("passengers", passengerList);

		Money totalPrice = object.getPrice();
		dataset.put("totalPrice", totalPrice);

		dataset.put("flight", object.getFlight().getTag());
		dataset.put("id", object.getId());

		super.getResponse().addData(dataset);
	}
}
