
package acme.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.entities.airline.Airline;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.entities.leg.LegRepository;
import acme.realms.Manager;

@Validator
public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	@Autowired
	private LegRepository repository;


	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {

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

		Flight flight = leg.getFlight();
		Manager manager = flight.getManager();
		Airline airline = manager.getAirline();
		if (!StringHelper.startsWith(leg.getFlightNumber(), airline.getIATACode(), true)) {
			super.state(context, false, "flightNumber", "acme.validation.leg.invalid-flight-number-manager.message");
			result = false;
		}

		if (leg.getDeparture() != null && leg.getArrival() != null && !leg.getArrival().after(leg.getDeparture())) {
			super.state(context, false, "arrival", "acme.validation.leg.invalid-schedule.message");
			result = false;
		}

		Integer flightNumber = leg.getFlight().getId();
		Collection<Leg> legs = this.repository.getLegsByFlight(flightNumber);

		if (legs.stream().anyMatch(x -> x.getId() != leg.getId() && x.getFlightNumber().equals(leg.getFlightNumber()))) {
			super.state(context, false, "flightNumber", "acme.validation.leg.duplicate-flight-number.message");
			result = false;
		}

		if (leg.getArrivalAirport() != null && leg.getDepartureAirport() != null && leg.getArrivalAirport().getId() == leg.getDepartureAirport().getId()) {
			super.state(context, false, "arrivalAirport", "acme.validation.leg.same-airports.message");
			result = false;
		}

		return result;
	}

	public static List<Leg> sortLegsByDeparture(final List<Leg> legs) {
		List<Leg> sortedLegs = new ArrayList<>(legs);
		sortedLegs.sort(Comparator.comparing(Leg::getDeparture));
		return sortedLegs;
	}
}
