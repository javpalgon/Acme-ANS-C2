
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.assignment.Assignment;
import acme.entities.flightcrewmember.AvailabilityStatus;
import acme.entities.leg.LegStatus;

@Validator
public class AssignmentValidator extends AbstractValidator<ValidAssignment, Assignment> {

	@Override
	protected void initialise(final ValidAssignment annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Assignment assignment, final ConstraintValidatorContext context) {
		if (assignment == null)
			return false;

		boolean memberAvailable = false;
		if (assignment.getMember() != null && assignment.getMember().getAvailabilityStatus() != null)
			memberAvailable = assignment.getMember().getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);

		boolean legLanded = false;
		if (assignment.getLeg() != null && assignment.getLeg().getStatus() != null)
			legLanded = assignment.getLeg().getStatus().equals(LegStatus.LANDED);

		super.state(context, memberAvailable || legLanded, "member", "{acme.validation.Assignment.memberNotAvailable.message}");

		return !super.hasErrors(context);
	}
}
