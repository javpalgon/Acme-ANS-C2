
package acme.entities.activitylog;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.assignment.Assignment;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ActivityLog extends AbstractEntity {

	// Serialisation version -------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes --------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registeredAt;

	@Mandatory
	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				incidentType;

	@Mandatory
	@NotBlank
	@ValidString(max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 10, integer = 2, fraction = 0)
	@Automapped
	private Integer				severityLevel;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Assignment			assignment;

}
