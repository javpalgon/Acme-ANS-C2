
package acme.entities.claim;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.leg.Leg;
import acme.entities.trackinglog.TrackingLog;
import acme.entities.trackinglog.TrackingLogRepository;
import acme.entities.trackinglog.TrackingLogStatus;
import acme.realms.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "leg_id"), @Index(columnList = "assistance_agent_id")
})
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
	@NotBlank
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidString(max = 255)
	@NotBlank
	@Automapped
	private String				description;

	@Valid
	@Mandatory
	@Automapped
	private ClaimType			type;

	@Mandatory
	@Automapped
	private Boolean				isDraftMode;

	// Derived attributes -----------------------------------------------------


	@Transient
	public ClaimStatus getAccepted() {
		ClaimStatus status = ClaimStatus.PENDING;
		TrackingLogRepository repository;
		repository = SpringHelper.getBean(TrackingLogRepository.class);
		List<TrackingLog> trackingLogs = repository.findAllByClaimId(this.getId());
		boolean predicate = trackingLogs.stream().anyMatch(x -> !x.getStatus().equals(TrackingLogStatus.PENDING) && !x.getIsDraftMode());
		if (predicate) {
			TrackingLog lastTrackingLog = trackingLogs.stream().filter(x -> x.getStatus() != TrackingLogStatus.PENDING).findAny().get();
			status = ClaimStatus.valueOf(lastTrackingLog.getStatus().toString());
		}
		return status;
	}

	// Relationships ----------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = true)
	private AssistanceAgent	assistanceAgent;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg				leg;

}
