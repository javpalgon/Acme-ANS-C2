
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.realms.Customer;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {

	@Override
	protected void initialise(final ValidCustomer annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		assert context != null;

		if (customer == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		DefaultUserIdentity userIdentity = customer.getIdentity();
		boolean identityIsValid = userIdentity != null && userIdentity.getName() != null && userIdentity.getSurname() != null;

		super.state(context, identityIsValid, "identifierNumber", "acme.validation.manager.null-identity.message");

		if (identityIsValid) {
			String[] surnameParts = userIdentity.getSurname().trim().split("\\s+");
			String initials = userIdentity.getName().trim().substring(0, 1);

			if (surnameParts.length > 0)
				initials += surnameParts[0].charAt(0);
			if (surnameParts.length > 1)
				initials += surnameParts[1].charAt(0);

			boolean identifierValid = StringHelper.startsWith(customer.getIdentifier(), initials, true);

			super.state(context, identifierValid, "identifier", "acme.validation.manager.wrong-initials.message");
		}

		return !super.hasErrors(context);
	}

}
