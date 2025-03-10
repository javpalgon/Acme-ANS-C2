
package acme.entities.Task;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;

public class Task extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@Automapped
	private TaskType			type;

	@Mandatory
	@NotBlank
	@ValidString(max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@PositiveOrZero
	@ValidNumber(min = 0, max = 10)
	@Automapped
	private Integer				priority;

	@Mandatory
	@Positive
	@ValidNumber
	@Automapped
	private Double				duration;

	//PONER RELACIONES AL MERGEAR

}
