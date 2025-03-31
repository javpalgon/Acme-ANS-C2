
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerShowService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	protected CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);

		Collection<Integer> customerIds = this.repository.findCustomerUserAccountIdsByPassengerId(id);
		int userAccountId = super.getRequest().getPrincipal().getAccountId();

		super.getResponse().setAuthorised(customerIds.contains(userAccountId));

	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findPassengerById(id);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void unbind(final Passenger object) {
		assert object != null;
		Dataset dataset = super.unbindObject(object, "fullName", "passport", "email", "birth", "specialNeeds");
		dataset.put("isDraftMode", object.getIsDraftMode());
		super.getResponse().addData(dataset);
	}
}
