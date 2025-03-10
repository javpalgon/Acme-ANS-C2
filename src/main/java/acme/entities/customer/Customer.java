
package acme.entities.customer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@NotBlank
	@Column(unique = true)
	@ValidString(pattern = "^[A-Z]{2-3}\\d{6}$")
	private String				idientifier;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@NotBlank
	@ValidString(max = 255)
	@Automapped
	private String				physicalAddress;

	@Mandatory
	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				city;

	@Mandatory
	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				country;

	@Optional
	@ValidNumber(min = 0, max = 500000)
	@Automapped
	private Integer				earnedPoints;

}
