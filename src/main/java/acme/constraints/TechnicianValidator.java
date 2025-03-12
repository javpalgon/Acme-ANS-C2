
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.entities.technician.Technician;

@Validator
public class TechnicianValidator extends AbstractValidator<ValidTechnician, Technician> {

	// Inicialización ---------------------------------------------------------

	@Override
	protected void initialise(final ValidTechnician annotation) {
		assert annotation != null;
	}

	// Validación -------------------------------------------------------------

	@Override
	public boolean isValid(final Technician technician, final ConstraintValidatorContext context) {
		assert context != null;

		if (technician == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		DefaultUserIdentity userIdentity = technician.getIdentity();
		boolean identityIsValid = userIdentity != null && userIdentity.getName() != null && userIdentity.getSurname() != null;

		super.state(context, identityIsValid, "identifierNumber", "acme.validation.technician.null-identity.message");

		if (identityIsValid) {
			String[] surnameParts = userIdentity.getSurname().trim().split("\\s+");
			String initials = userIdentity.getName().trim().substring(0, 1);

			if (surnameParts.length > 0)
				initials += surnameParts[0].charAt(0);
			if (surnameParts.length > 1)
				initials += surnameParts[1].charAt(0);

			boolean identifierValid = StringHelper.startsWith(technician.getLicenseNumber(), initials, true);

			super.state(context, identifierValid, "identifier", "acme.validation.technician.wrong-initials.message");
		}

		// Retornar si hay errores en la validación
		return !super.hasErrors(context);
	}
}
