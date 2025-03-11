
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.entities.leg.LegRepository;

@Validator
public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	// Dependencias internas --------------------------------------------------

	@Autowired
	private LegRepository repository;

	// Implementación de ConstraintValidator ----------------------------------


	@Override
	protected void initialise(final ValidFlight annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Flight flight, final ConstraintValidatorContext context) {
		assert context != null;

		if (flight == null || this.repository.findAllLegsByFlightId(flight.getId()) == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		List<Leg> legs = this.repository.findAllLegsByFlightId(flight.getId());

		for (int i = 0; i < legs.size() - 1; i++) {
			Leg currentLeg = legs.get(i);
			Leg nextLeg = legs.get(i + 1);

			if (!MomentHelper.isBefore(currentLeg.getArrival(), nextLeg.getDeparture())) {
				super.state(context, false, "legs", "acme.validation.flight.invalid-schedule-legs.message");
				return false;
			}
		}

		return true;
	}

}
