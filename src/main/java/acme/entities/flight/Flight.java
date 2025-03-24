
package acme.entities.flight;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
import acme.entities.leg.LegRepository;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@NotBlank
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Automapped
	private Boolean				requiresSelfTransfer;

	@Mandatory
	@ValidMoney(min = 0, max = 10000000)
	@Automapped
	private Money				cost;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				description;


	@Transient
	public Date getDeparture() {
		Date result;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		result = repository.findDepartureByFlightId(this.getId()).stream().toList().get(0);

		return result;
	}

	@Transient
	public Date getArrival() {
		Date result;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		result = repository.findArrivalByFlightId(this.getId()).stream().toList().get(0);
		return result;
	}

	@Transient
	public String getOriginCity() {
		String result;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		result = repository.findOriginCityByFlightId(this.getId()).stream().toList().get(0);

		return result;
	}

	@Transient
	public String getDestinationCity() {
		String result;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		result = repository.findDestinationCityByFlightId(this.getId()).stream().toList().get(0);

		return result;
	}

	@Transient
	public Integer getNumOfLayovers() {
		Integer result;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		result = repository.findNumberOfLayovers(this.getId()) - 1;

		return result;
	}


	@Optional
	@ManyToOne(optional = true)
	@Valid
	private Manager manager;

}
