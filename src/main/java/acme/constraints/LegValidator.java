
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.entities.leg.Leg;

@Validator
public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	@Override
	protected void initialise(final ValidLeg constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {
		assert context != null;

		if (leg == null || leg.getFlight() == null || leg.getAirline() == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		boolean validSchedule = MomentHelper.isAfter(leg.getArrival(), leg.getDeparture());
		super.state(context, validSchedule, "schedule", "acme.validation.leg.invalid-schedule.message");

		String airlineCode = leg.getAirline().getIATACode();
		boolean validNumber = StringHelper.startsWith(leg.getFlightNumber(), airlineCode, true);
		super.state(context, validNumber, "flightNumber", "acme.validation.leg.invalid-flight-number.message");

		return !super.hasErrors(context);
	}
}
