
package acme.entities.claim;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Claim extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registeredAt;

	@Mandatory
	@ValidEmail
	@Automapped
	@NotBlank
	private String				passengerEmail;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	@NotBlank
	private String				description;

	@Valid
	@Mandatory
	@Automapped
	private ClaimType			type;

	@Mandatory
	@Automapped
	private Boolean				accepted;

	// Relationships ----------------------------------------

	/*
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @ManyToOne
	 * private AssistanceAgent assistanceAgent;
	 * 
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @ManyToOne
	 * private Leg leg;
	 */

}
