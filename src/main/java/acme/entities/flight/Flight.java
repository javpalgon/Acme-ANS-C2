
package acme.entities.flight;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidFlight;
import acme.entities.leg.Leg;
import acme.entities.leg.LegRepository;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidFlight
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


	@Transient
	public Date getDeparture() {
		Date result;
		Leg leg;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		leg = repository.findFirstLegByFlightId(this.getId());
		result = leg.getDeparture();

		return result;
	}

	@Transient
	public Date getArrival() {
		Date result;
		Leg leg;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		leg = repository.findLastLegByFlightId(this.getId());
		result = leg.getArrival();

		return result;
	}

	@Transient
	public String getOriginCity() {
		String result;
		Leg leg;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		leg = repository.findFirstLegByFlightId(this.getId());
		result = leg.getDepartureAP().getCity();

		return result;
	}

	@Transient
	public String getDestinationCity() {
		String result;
		Leg leg;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		leg = repository.findLastLegByFlightId(this.getId());
		result = leg.getArrivalAP().getCity();

		return result;
	}

	@Transient
	public Integer getNumOfLayovers() {
		Integer result;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		result = repository.numLegsByFlightId(this.getId()) - 1;

		return result;
	}

}
