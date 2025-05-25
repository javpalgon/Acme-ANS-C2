
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimRepository;
import acme.entities.trackinglog.TrackingLogRepository;

@Validator
public class ClaimValidator extends AbstractValidator<ValidClaim, Claim> {

	@Autowired
	private ClaimRepository			repository;

	@Autowired
	private TrackingLogRepository	trackingLogRepository;


	@Override
	protected void initialise(final ValidClaim annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Claim value, final ConstraintValidatorContext context) {
		/*
		 * assert context != null;
		 * boolean res = false;
		 * boolean isNull = value == null || value.getLeg() == null || value.getRegisteredAt() == null || value.getLeg().getFlight() == null;
		 * 
		 * if (isNull)
		 * super.state(context, false, "*", "javax.validation.constraints.notNull.message");
		 * else {
		 * 
		 * //Comprobar que la fecha de llegada del leg asociado a la Claim es antes que la fecha de creación de la Claim
		 * boolean predicate;
		 * predicate = value.getLeg().getArrival().before(value.getRegisteredAt());
		 * super.state(context, predicate, "registeredAt", "acme.validation.Claim.registeredAtIsBeforeArrival");
		 * 
		 * //Comprobar que el Leg asociado a la Claim están publicados.
		 * boolean predicate2;
		 * predicate2 = value.getLeg().getIsDraftMode();
		 * super.state(context, !predicate2, "*", "acme.validation.Claim.legIsDraftMode");
		 * 
		 * //Comprobar que el Flight asociado a la Claim están publicados.
		 * boolean predicate3;
		 * predicate3 = value.getLeg().getFlight().getIsDraftMode();
		 * super.state(context, !predicate3, "*", "acme.validation.Claim.flightIsDraftMode");
		 * 
		 * // COMPROBAR QUE EL AGENT ASOCIADO Y EL AIRCRAFT DEL LEG SON DE LA MISMA AEROLÍNEA.
		 * boolean predicate4;
		 * predicate4 = value.getAssistanceAgent().getAirline().equals(value.getLeg().getAircraft().getAirline());
		 * super.state(context, predicate4, "leg", "acme.validation.Claim.airline-doesn´t-match");
		 * //}
		 * res = !super.hasErrors(context);
		 * return res;
		 */
		return true;
	}

}
