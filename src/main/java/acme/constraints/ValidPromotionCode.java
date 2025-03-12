
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
@Constraint(validatedBy = PromotionCodeValidator.class)
public @interface ValidPromotionCode {

	String message() default "Invalid promotion code+ {acme.validation.text.message}";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
