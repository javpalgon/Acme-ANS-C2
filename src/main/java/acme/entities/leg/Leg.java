
package acme.entities.leg;

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
	@Index(name = "idx_leg_flight", columnList = "flight_id"), @Index(name = "idx_leg_flight_draft", columnList = "flight_id, isDraftMode"), @Index(name = "idx_leg_departure_airport", columnList = "departure_airport_id"),
	@Index(name = "idx_leg_arrival_airport", columnList = "arrival_airport_id")
})

public class Leg extends AbstractEntity {

	// Serialisation version -------------------------------------------
	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 7, max = 7, pattern = "^[A-Z]{3}\\d{4}$", message = "{acme.validation.leg.flight-number-pattern.message}")
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
		Double duration = null;

		if (this.getDeparture() != null && this.getArrival() != null && MomentHelper.isAfterOrEqual(this.getArrival(), this.getDeparture()))
			duration = MomentHelper.computeDuration(this.getDeparture(), this.getArrival()).getSeconds() / 3600.;

		return duration;
	}

}
