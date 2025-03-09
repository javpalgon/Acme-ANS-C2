
package acme.entities.leg;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;

public class Leg extends AbstractEntity {

	// Serialisation version -------------------------------------------
	private static final long	serialVersionUID	= 1L;

	@ValidString(pattern = "^[A-Z]{2,3}\\d{4}$")
	@Column(unique = true)
	@Mandatory
	private String				flightNumber;

	@ValidMoment
	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	@Automapped
	private Date				departure;

	@ValidMoment
	@Mandatory
	@Automapped
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

	// Relations --------------------------------------------------------

	/*
	 * (Leg N -> 1 (departure) Airport )
	 * 
	 * @ManyToOne
	 * 
	 * @
	 * private Airport departureAP;
	 */

	/*
	 * (Leg N -> 1 (arrival) Airport )
	 * 
	 * @ManyToOne
	 * 
	 * @
	 * private Airport arrivalAP;
	 */

	/*
	 * (Leg 1 -> 1 Aircraft )
	 * 
	 * @OneToOne
	 * 
	 * @
	 * private Aircraft aircraft;
	 */

}
