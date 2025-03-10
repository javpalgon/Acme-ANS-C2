
package acme.entities.flight;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Automapped
	@Valid
	private Boolean				requiresSelfTransfer;

	@Mandatory
	@Automapped
	@ValidMoney
	private Money				cost;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				description;

	// the rest are derived attributes, we need the repository

	@ValidMoment
	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	private Date				departure;

	@ValidMoment
	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	private Date				arrival;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				originCity;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				destinationCity;

	@ValidNumber(min = 0, max = 15)
	@Mandatory
	@Automapped
	private Integer				layovers;

}
