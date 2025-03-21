
package acme.constraints;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;

@Validator
public class BirthdayValidator extends AbstractValidator<ValidBirthday, Date> {

	private static final int MIN_AGE = 16;


	@Override
	protected void initialise(final ValidBirthday annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Date birthdate, final ConstraintValidatorContext context) {
		if (birthdate == null)
			return false;

		LocalDate birthLocalDate = birthdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate today = LocalDate.now();

		long age = ChronoUnit.YEARS.between(birthLocalDate, today);

		if (age < BirthdayValidator.MIN_AGE) {
			super.state(context, false, "birthdate", "acme.validation.Birthday.invalid-age.message");
			return false;
		}

		return true;
	}
}
