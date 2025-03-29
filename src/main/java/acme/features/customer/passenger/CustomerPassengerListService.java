
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int bookingId = super.getRequest().getData("bookingId", int.class);
		Collection<Passenger> passengers = this.repository.findPassengersByBookingId(bookingId);
		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger object) {
		assert object != null;
		Dataset dataset = super.unbindObject(object, "fullName", "passport", "email", "birth", "specialNeeds");
		super.getResponse().addData(dataset);
	}
}
