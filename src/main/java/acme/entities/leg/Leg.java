
package acme.entities.leg;

import java.time.Duration;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.MomentHelper;
import acme.constraints.ValidLeg;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidLeg
@Table(name = "leg", indexes = {
	@Index(name = "idx_leg_status_draft", columnList = "status, isDraftMode"), @Index(name = "idx_leg_draft", columnList = "isDraftMode"), @Index(name = "idx_leg_flight_departure", columnList = "flight_id, departure")
})
public class Leg extends AbstractEntity {

	// Serialisation version -------------------------------------------
	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{3}[0-9]{4}$", message = "{acme.validation.leg.number}")
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				departure;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				arrival;

	@Mandatory
	@Automapped
	private LegStatus			status;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport				departureAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport				arrivalAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight				flight;

	@Mandatory
	@Automapped
	private Boolean				isDraftMode;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft			aircraft;


	@Transient
	public Double getDuration() {
		Duration duration;
		Double result;
		duration = MomentHelper.computeDuration(this.departure, this.arrival);
		result = duration.toMinutes() / 60.0;
		return result;

	}

}
