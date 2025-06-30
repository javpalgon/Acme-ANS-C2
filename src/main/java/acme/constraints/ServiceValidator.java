
package acme.constraints;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.service.Service;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	@Override
	protected void initialise(final ValidService annotation) {
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {
		boolean isCostCorrect = true;
		Set<String> VALID_CURRENCIES = Currency.getAvailableCurrencies().stream().map(Currency::getCurrencyCode).filter(code -> !code.equals("XXX")).collect(Collectors.toSet());

		if (service.getDiscount() != null)
			if (!VALID_CURRENCIES.contains(service.getDiscount().getCurrency()))
				isCostCorrect = false;

		super.state(context, isCostCorrect, "cost", "acme.validation.flight.invalid-cost.message");

		return !super.hasErrors(context);
	}
}
