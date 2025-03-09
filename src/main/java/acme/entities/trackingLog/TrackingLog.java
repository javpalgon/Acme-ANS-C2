
package acme.entities.trackingLog;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
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
public class TrackingLog extends AbstractEntity {

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdate;

	@Mandatory
	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				step;

	@Mandatory
	@ValidNumber(min = 0., max = 100.)
	@Automapped
	private Double				resolutionPercentage;

	@Mandatory
	@Valid
	@Automapped
	private TrackingLogStatus	status;

	@Optional
	@Automapped
	@ValidString(max = 255)
	private String				resolution;

	// Relationships --------------------------------------------------------

	// RELACION CON CLAIM

	/*
	 * IMPLEMENTAR RESTRICCIÓN:
	 * Si status == PENDING, resolution tiene que ser null y resolutionPercentage!== 100 y cuando status !== PENDING,
	 * resolution tiene q ser mandatory y ademas resolutionPercentage==100
	 */
}
