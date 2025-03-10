
package acme.entities.Aircraft;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;

public class Aircraft extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				model;

	@Mandatory
	@NotBlank
	@ValidString(max = 50)
	@Column(unique = true)
	private String				regitrationNumber;

	@Mandatory
	@Positive
	@Automapped
	private Integer				capacity;

	@Mandatory
	@ValidNumber(min = 2000, max = 50000)
	@Automapped
	private Integer				cargoWeight;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				active;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				details;

	//RELATIONSHIPS

	//	@Mandatory
	//	@Valid
	//	@ManyToOne
	//	private Airline				airline;

}
