
package acme.entities.service;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidPromotionCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Service extends AbstractEntity {

	// Serialisation version -------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes --------------------------------------------------------

	@Mandatory
	@NotBlank
	@ValidString(max = 50)
	@Automapped
	private String				name;

	@Mandatory
	@NotBlank
	@ValidUrl
	@Automapped
	private String				pictureLink;

	@Mandatory
	@ValidNumber(min = 0.0, max = 100.0, fraction = 2)
	@Automapped
	private Double				averageDwellTime;

	@Optional
	@ValidString(pattern = "^[A-Z]{4}-[0-9]{2}$")
	@ValidPromotionCode
	private String				promotionCode;

	@Optional
	@ValidMoney(min = 0.0, max = 1000000.0)
	@Automapped
	private Money				discount;

}
