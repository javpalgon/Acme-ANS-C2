
package acme.entities.flight;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Moment;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;

public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Automapped
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
	@Automapped
	private Moment				departure;

	@ValidMoment
	@Mandatory
	@Automapped
	private Moment				arrival;

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

	// Relations --------------------------------------------------------

	/*
	 * (Flight N -> 1 AirlineManager)
	 * 
	 * @ManyToOne
	 * 
	 * @
	 * private AirlineManager manager;
	 */

}
