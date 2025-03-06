
package acme.entities.Review;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Review extends AbstractEntity {

	// Serialisation version -------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes --------------------------------------------------------

	@Mandatory
	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				alias;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				postedAt;

	@Mandatory
	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				subject;

	@Mandatory
	@NotBlank
	@ValidString(max = 255)
	@Automapped
	private String				text;

	@Optional
	@ValidNumber(min = 0, max = 10, integer = 2, fraction = 2)
	@Automapped
	private Double				score;

	@Optional
	@Automapped
	private Boolean				recommended;
}
