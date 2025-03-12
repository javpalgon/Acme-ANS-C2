
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.realms.Member;

@Validator
public class MemberValidator extends AbstractValidator<ValidMember, Member> {

	@Override
	protected void initialise(final ValidMember annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Member value, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = false;

		if (value == null || value.getEmployeeCode() == null || value.getIdentity() == null || value.getIdentity().getName() == null || value.getIdentity().getSurname() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String initials = "";
			String res = "";
			String name = value.getIdentity().getName();
			String[] surname = StringHelper.splitChoices(value.getIdentity().getSurname());
			initials += name.charAt(0);
			for (String surnamePart : surname) {
				char initial = surnamePart.charAt(0);
				initials += initial;
			}
			res = initials.substring(0, Math.min(3, initials.length()));

			// VALIDATE THAT THE EMPLOYEE CODE DOES START WITH THE INITIALS OF THE FIRST AND LAST NAME
			super.state(context, StringHelper.startsWith(value.getEmployeeCode(), res, false), "employeeCode", "acme.validation.Member.employeeCode-initials");

			// VALIDATE THE FORMAT OF THE EMPLOYEE CODE
			super.state(context, value.getEmployeeCode().matches("^[A-Z]{2,3}\\d{6}$"), "employeeCode", "acme.validation.Member.employeeCode-format");
		}
		result = !super.hasErrors(context);
		return result;
	}
}
