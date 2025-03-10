
package acme.entities.passenger;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Passenger extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@NotBlank
	@Automapped
	@ValidString(min = 1, max = 255)
	private String				fullName;

	@Mandatory
	@NotBlank
	@Automapped
	@ValidEmail
	private String				email;

	@Mandatory
	@Column(unique = true)
	@ValidString(pattern = "^[A-Z0-9]{6,9}$")
	private String				passport;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	private Date				birth;

	@Optional
	@Automapped
	@ValidString(min = 1, max = 50)
	private String				specialNeeds;

}
