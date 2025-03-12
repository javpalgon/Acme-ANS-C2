
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidMember;
import acme.entities.airline.Airline;
import acme.entities.assignment.Assignment;
import acme.entities.flightcrewmember.AvailabilityStatus;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidMember
public class Member extends AbstractRole {

	// Serialisation version -------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes --------------------------------------------------------

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
	@Valid
	@Automapped
	private AvailabilityStatus	availabilityStatus;

	@Mandatory
	@ValidMoney(min = 1., max = 1000000.0)
	@Automapped
	private Money				salary;

	@Optional
	@ValidNumber(min = 0, max = 60)
	@Automapped
	private Integer				yearsOfExperience;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline;

	@Optional
	@Valid
	@ManyToOne(optional = true)
	private Assignment			assignment;

}
