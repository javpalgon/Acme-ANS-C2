
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({
	ElementType.FIELD,         // Permite aplicarlo en atributos de clases
	ElementType.METHOD,        // Permite aplicarlo en métodos (getters/setters)
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BirthdayValidator.class)
public @interface ValidBirthday {

	String message() default "Invalid birthday+ {acme.validation.Birthday.invalid-age.message}";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
