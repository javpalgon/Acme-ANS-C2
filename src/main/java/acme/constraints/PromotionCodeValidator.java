
package acme.constraints;

import java.time.LocalDate;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;

@Validator
public class PromotionCodeValidator extends AbstractValidator<ValidPromotionCode, String> {

	@Override
	protected void initialise(final ValidPromotionCode annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final String promotionCode, final ConstraintValidatorContext context) {

		if (promotionCode == null || promotionCode.isEmpty())
			return true;
		else {
			if (!promotionCode.matches("^[A-Z]{4}-[0-9]{2}$")) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("acme.validation.Service.invalid-promotionCode.message").addConstraintViolation();
				return false;
			}

			String currentYear = String.valueOf(LocalDate.now().getYear()).substring(2);
			String last2digits = promotionCode.substring(promotionCode.length() - 2);
			if (!currentYear.equals(last2digits)) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("acme.validation.Service.invalid-promotionCode.message").addConstraintViolation();
				return false;
			}
		}
		return true;
	}

}
