
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.entities.assistanceagent.AssistanceAgent;
import acme.entities.assistanceagent.AssistanceAgentRepository;

@Validator
public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

	@Autowired
	private AssistanceAgentRepository repository;


	@Override
	protected void initialise(final ValidAssistanceAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AssistanceAgent value, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = false;

		if (value == null || value.getEmployeeCode() == null || value.getIdentity() == null || value.getIdentity().getName() == null || value.getIdentity().getSurname() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String initials = "";
			String res = "";
			String name = value.getUserAccount().getIdentity().getName();
			String[] surname = StringHelper.splitChoices(value.getUserAccount().getIdentity().getSurname());
			initials += name.charAt(0);
			for (String surnamePart : surname) {
				char initial = surnamePart.charAt(0);
				initials += initial;
			}
			res = initials.substring(0, Math.min(3, initials.length()));
			super.state(context, StringHelper.startsWith(value.getEmployeeCode(), res, false), "employeeCode", "acme.validation.AssistanceAgent.employeeCode-initials");
		}
		result = !super.hasErrors(context);
		return result;
	}
}
