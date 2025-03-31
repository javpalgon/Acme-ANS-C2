
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerUpdateService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Collection<Integer> customerIds = this.repository.findCustomerUserAccountIdsByPassengerId(id);
		int userAccountId = super.getRequest().getPrincipal().getAccountId();

		super.getResponse().setAuthorised(customerIds.contains(userAccountId));
	}

	@Override
	public void load() {
		Passenger passenger;
		int id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger object) {
		super.bindObject(object, "fullName", "passport", "email", "birth", "specialNeeds");
	}

	@Override
	public void validate(final Passenger object) {
		assert object != null;

		// Solo se puede modificar si está en modo borrador
		super.state(object.getIsDraftMode(), "*", "customer.passenger.form.error.not-draft");

		// Validación de fullName
		super.state(object.getFullName() != null && !object.getFullName().trim().isEmpty(), "fullName", "customer.passenger.form.error.fullName.required");

		// Validación de email
		super.state(object.getEmail() != null && object.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"), "email", "customer.passenger.form.error.email.invalid");

		// Validación de passport
		super.state(object.getPassport() != null && object.getPassport().matches("^[A-Z0-9]{6,9}$"), "passport", "customer.passenger.form.error.passport.invalid");
		boolean isDuplicate = this.repository.existsByPassport(object.getPassport(), object.getId());
		super.state(!isDuplicate, "passport", "customer.passenger.form.error.duplicate-passport");

		// Validación de birth (fecha en el pasado)
		boolean isPast = object.getBirth() != null && MomentHelper.isBefore(object.getBirth(), MomentHelper.getCurrentMoment());
		super.state(isPast, "birth", "customer.passenger.form.error.birth.past");

		// Validación de specialNeeds (si se indica)
		if (object.getSpecialNeeds() != null)
			super.state(object.getSpecialNeeds().length() <= 50, "specialNeeds", "customer.passenger.form.error.specialNeeds.length");
	}

	@Override
	public void perform(final Passenger object) {
		this.repository.save(object);
	}

	@Override
	public void unbind(final Passenger object) {
		Dataset dataset = super.unbindObject(object, "fullName", "passport", "email", "birth", "specialNeeds");
		dataset.put("isDraftMode", object.getIsDraftMode());

		super.getResponse().addData(dataset);
	}
}
