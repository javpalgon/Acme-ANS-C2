
package acme.entities.Assignment;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Assignment extends AbstractEntity {

	// Serialisation version -------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes --------------------------------------------------------

	@Mandatory
	@Automapped
	@Valid
	private Role				role;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdate;

	@Mandatory
	@Automapped
	@Valid
	private AssignmentStatus	status;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				remarks;

	// Relations --------------------------------------------

	/*
	 * -- Assignment 4..N -> 1 Leg
	 * 
	 * @Mandatory
	 * 
	 * @ManyToOne
	 * 
	 * @Valid
	 * private Leg leg;
	 * 
	 * -- Assignment N -> 0..1 Flight Crew Member
	 * 
	 * @Optional
	 * 
	 * @ManyToOne
	 * 
	 * @Valid
	 * private FlightCrewMember flightCrewMember;
	 */

}
