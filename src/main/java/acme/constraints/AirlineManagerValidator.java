
package acme.constraints;

import java.util.Collection;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.entities.leg.LegRepository;
import acme.realms.Manager;

@Validator
public class AirlineManagerValidator extends AbstractValidator<ValidAirlineManager, Manager> {

	@Autowired
	private LegRepository repository;

	// Initialization ---------------------------------------------------------


	@Override
	protected void initialise(final ValidAirlineManager annotation) {
		assert annotation != null;
	}

	// Validation -------------------------------------------------------------

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {
		// HINT: manager can be null
		assert context != null;

		boolean result;

		if (manager == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			DefaultUserIdentity identity = manager.getIdentity();
			String nameInitial = identity.getName().trim().substring(0, 1);

			String[] surnameParts = identity.getSurname().trim().split("\\s+");
			StringBuilder surnameInitials = new StringBuilder();

			for (int i = 0; i < Math.min(2, surnameParts.length); i++)
				if (!surnameParts[i].isEmpty())
					surnameInitials.append(surnameParts[i].substring(0, 1));

			String initials = nameInitial + surnameInitials.toString();

			Boolean correctName = StringHelper.startsWith(manager.getIdentifier(), initials, true);

			super.state(context, correctName, "identifier", "acme.validation.manager.wrong-initials.message");
		}

		Collection<Manager> managers = this.repository.getAllManagers();

		if (managers.stream().anyMatch(x -> x.getId() != manager.getId() && x.getIdentifier().equals(manager.getIdentifier())))
			super.state(context, false, "identifier", "acme.validation.manager.not-unique.message");

		result = !super.hasErrors(context);

		return result;
	}
}
