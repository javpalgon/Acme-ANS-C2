
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.assignment.Assignment;
import acme.entities.flightcrewmember.AvailabilityStatus;
import acme.features.member.assignment.MemberAssignmentRepository;

@Validator
public class AssignmentValidator extends AbstractValidator<ValidAssignment, Assignment> {

	@Autowired
	private MemberAssignmentRepository repository;


	@Override
	protected void initialise(final ValidAssignment annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Assignment assignment, final ConstraintValidatorContext context) {
		if (assignment == null || assignment.getMember() == null)
			return false;

		boolean memberAvailable;
		memberAvailable = assignment.getMember().getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);
		super.state(context, memberAvailable, "member", "{acme.validation.Assignment.memberNotAvailable.message}");

		boolean result = !super.hasErrors(context);
		return result;
	}
}
