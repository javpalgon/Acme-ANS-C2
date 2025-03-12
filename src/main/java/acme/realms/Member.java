
package acme.entities.flightcrewmember;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.airline.Airline;
import acme.entities.assignment.Assignment;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FlightCrewMember extends AbstractEntity {

	// Serialisation version -------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes --------------------------------------------------------

	//TODO: Add validation class
	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Automapped
	private String				employeeCode;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Column(unique = true)
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				languageSkills;

	@Mandatory
	@Automapped
	@Valid
	private AvailabilityStatus	availabilityStatus;

	@Mandatory
	@ValidMoney(min = 1., max = 1000000.0)
	@Automapped
	private Money				salary;

	@Optional
	@Automapped
	@ValidNumber(min = 0, max = 60)
	private Integer				yearsOfExperience;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Airline				airline;

	@Optional
	@Valid
	@ManyToOne(optional = true)
	private Assignment			assignment;

}
