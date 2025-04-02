
package acme.constraints;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.realms.Manager;

@Validator
public class AirlineManagerValidator extends AbstractValidator<ValidAirlineManager, Manager> {

	// Minimum experience validation constants
	private final int MIN_AGE_FOR_EXPERIENCE = 16;

	// Initialization ---------------------------------------------------------


	@Override
	protected void initialise(final ValidAirlineManager annotation) {
		assert annotation != null;
	}

	private String sanitizeInitials(final String initials) {
		StringBuilder sanitized = new StringBuilder();

		for (char c : initials.toCharArray())
			if (Character.isLetter(c)) {
				// Convert to uppercase Latin if possible
				String upper = String.valueOf(c).toUpperCase();

				// Only keep Latin letters A-Z
				if (upper.matches("[A-Z]"))
					sanitized.append(upper);
			}

		return sanitized.toString();
	}

	// Validation -------------------------------------------------------------

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {
		assert context != null;

		if (manager == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		LocalDate birthDate = manager.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate today = LocalDate.now();

		long age = ChronoUnit.YEARS.between(birthDate, today);

		if (manager.getYearsOfExperience() > age - this.MIN_AGE_FOR_EXPERIENCE) {
			super.state(context, false, "*", "acme.validation.Experience.invalid-experience.message");
			return false;
		}

		DefaultUserIdentity userIdentity = manager.getIdentity();
		String[] surnameParts = userIdentity.getSurname().trim().split("\\s+");
		String initials = userIdentity.getName().trim().substring(0, 1);

		if (surnameParts.length > 0)
			initials += surnameParts[0].charAt(0);
		if (surnameParts.length > 1)
			initials += surnameParts[1].charAt(0);

		// ✨ New line: sanitize to A-Z only
		String sanitizedInitials = this.sanitizeInitials(initials);

		// ✨ Now compare using sanitized version
		boolean identifierValid = StringHelper.startsWith(manager.getIdentifier(), sanitizedInitials, true);
		super.state(context, identifierValid, "identifier", "acme.validation.manager.wrong-initials.message");

		// Return true if no errors
		return !super.hasErrors(context);
	}
}
