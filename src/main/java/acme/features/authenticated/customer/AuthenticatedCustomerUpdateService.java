
package acme.features.authenticated.customer;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.Customer;

@GuiService
public class AuthenticatedCustomerUpdateService extends AbstractGuiService<Authenticated, Customer> {

	@Autowired
	private AuthenticatedCustomerRepository repository;


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Customer customer;
		int userAccountId;
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		customer = this.repository.findCustomerByUserAccountId(userAccountId);
		super.getBuffer().addData(customer);
	}

	@Override
	public void bind(final Customer object) {
		super.bindObject(object, "identifier", "phoneNumber", "physicalAddress", "city", "country", "earnedPoints");
	}

	@Override
	public void validate(final Customer object) {
		assert object != null;
	}

	@Override
	public void perform(final Customer object) {
		this.repository.save(object);
	}

	@Override
	public void unbind(final Customer object) {
		Dataset dataset;
		dataset = super.unbindObject(object, "identifier", "phoneNumber", "physicalAddress", "city", "country", "earnedPoints");
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
