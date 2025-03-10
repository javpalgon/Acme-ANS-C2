
package acme.entities.leg;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Leg extends AbstractEntity {

	// Serialisation version -------------------------------------------
	private static final long	serialVersionUID	= 1L;

	//TODO: Add validation class

	@ValidString(pattern = "^[A-Z]{2,3}\\d{4}$")
	@Column(unique = true)
	@Mandatory
	private String				flightNumber;

	@ValidMoment
	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	private Date				departure;

	@ValidMoment
	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	private Date				arrival;

	@ValidNumber(min = 0.1, max = 20.)
	@Mandatory
	@Automapped
	private Double				duration;

	@Mandatory
	@Automapped
	@Valid
	private LegStatus			status;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Airport				departureAP;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Airport				arrivalAP;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Flight				flight;

}
