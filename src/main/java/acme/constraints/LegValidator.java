
package acme.constraints;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.entities.airline.Airline;
import acme.entities.leg.Leg;
import acme.entities.leg.LegRepository;

public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	@Autowired
	private LegRepository repository;


	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (leg == null)
			super.state(context, false, "*", "acme.validation.NotNull.message");
		else {
			Airline airline = leg.getAircraft().getAirline();
			String IATAcode = airline.getIATACode();
			if (!StringHelper.startsWith(leg.getFlightNumber(), IATAcode, true))
				super.state(context, false, "legs", "acme.validation.leg.invalid-flight-number.message");
			else if (leg.getArrival().before(leg.getDeparture()))
				super.state(context, false, "legs", "acme.validation.leg.invalid-schedule.message");
			else {
				boolean correctLeg = true;
				List<Leg> legs = new ArrayList<>(this.repository.findAllLegsByFlightId(leg.getFlight().getId()));
				legs.add(leg);

				legs = LegValidator.sortLegsByDeparture(legs);
				for (int i = 0; i < legs.size() - 1 && correctLeg && legs.size() < 2; i++) {
					if (legs.get(i).getArrival().after(legs.get(i + 1).getDeparture()))
						correctLeg = false;
					if (!legs.get(i).getArrivalAirport().getCity().equals(legs.get(i + 1).getDepartureAirport().getCity()))
						correctLeg = false;
				}
				super.state(context, correctLeg, "legs", "acme.validation.leg.invalid-legs.message");

			}
		}
		result = !super.hasErrors(context);

		return result;
	}

	public static List<Leg> sortLegsByDeparture(final List<Leg> legs) {
		List<Leg> sortedLegs = new ArrayList<>(legs);
		sortedLegs.sort(Comparator.comparing(Leg::getDeparture));
		return sortedLegs;
	}
}
