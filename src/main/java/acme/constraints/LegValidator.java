
package acme.constraints;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.entities.leg.Leg;

public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {
		assert context != null;

		if (leg == null) {
			super.state(context, false, "*", "acme.validation.NotNull.message");
			return false;
		}

		boolean result = true;

		if (leg.getAircraft() != null && leg.getAircraft().getAirline() != null)
			if (!StringHelper.startsWith(leg.getFlightNumber(), leg.getAircraft().getAirline().getIATACode(), true)) {
				super.state(context, false, "flightNumber", "acme.validation.leg.invalid-flight-number.message");
				result = false;
			}

		// 2. Validar que la hora de llegada es posterior a la de salida
		if (leg.getDeparture() != null && leg.getArrival() != null && !leg.getArrival().after(leg.getDeparture())) {
			super.state(context, false, "arrival", "acme.validation.leg.invalid-schedule.message");
			result = false;
		}

		// 3. Validar que los aeropuertos son distintos
		if (leg.getArrivalAirport() != null && leg.getDepartureAirport() != null && leg.getArrivalAirport().getId() == leg.getDepartureAirport().getId()) {
			super.state(context, false, "arrivalAirport", "acme.validation.leg.same-airports.message");
			result = false;
		}

		if (leg.getDeparture() != null) {
			// Get the current moment
			Date currentMoment = MomentHelper.getCurrentMoment();

			// Calculate the time 24 hours later
			Date twentyFourHoursLater = new Date(currentMoment.getTime() + 24 * 60 * 60 * 1000); // 24 hours in milliseconds

			// Check if the departure is after the current moment and not within the next 24 hours
			if (leg.getDeparture().after(currentMoment) && leg.getDeparture().before(twentyFourHoursLater)) {
				super.state(context, false, "departure", "acme.validation.leg.invalid-departure-date.message");
				result = false;
			}
		}

		return result;
	}

	public static List<Leg> sortLegsByDeparture(final List<Leg> legs) {
		List<Leg> sortedLegs = new ArrayList<>(legs);
		sortedLegs.sort(Comparator.comparing(Leg::getDeparture));
		return sortedLegs;
	}
}
