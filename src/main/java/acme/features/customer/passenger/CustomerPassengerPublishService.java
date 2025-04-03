
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerPublishService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findPassengerById(id);

		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		super.getResponse().setAuthorised(passenger.getIsDraftMode() && this.repository.findCustomerUserAccountIdsByPassengerId(id).contains(userAccountId));
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findPassengerById(id);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger object) {
		assert object != null;
		super.bindObject(object, "fullName", "passport", "email", "birth", "specialNeeds");
	}

	@Override
	public void validate(final Passenger object) {
		assert object != null;

	}

	@Override
	public void perform(final Passenger object) {
		assert object != null;
		object.setIsDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Passenger object) {
		Dataset dataset = super.unbindObject(object, "fullName", "passport", "email", "birth", "specialNeeds", "isDraftMode");
		super.getResponse().addData(dataset);
	}
}
