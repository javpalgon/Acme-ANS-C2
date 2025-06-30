
package acme.entities.flight;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidFlight;
import acme.entities.leg.LegRepository;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidFlight
@Table(name = "flight", indexes = {
	@Index(name = "idx_flight_draft_mode", columnList = "isDraftMode")
})
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				requiresSelfTransfer;

	@Mandatory
	@ValidMoney(min = 0, max = 1000000)
	@Automapped
	private Money				cost;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Automapped
	private Boolean				isDraftMode;

	// Derived attributes


	private LegRepository legRepository() {
		return SpringHelper.getBean(LegRepository.class);
	}

	@Transient
	public Date getDeparture() {
		if (!this.legRepository().hasLegs(this.getId()))
			return null;

		return this.legRepository().findDepartureByFlightId(this.getId()).stream().findFirst().orElse(null);
	}

	@Transient
	public Date getArrival() {
		if (!this.legRepository().hasLegs(this.getId()))
			return null;

		return this.legRepository().findArrivalByFlightId(this.getId()).stream().findFirst().orElse(null);
	}

	@Transient
	public String getOriginCity() {
		if (!this.legRepository().hasLegs(this.getId()))
			return null;

		return this.legRepository().findOriginCityByFlightId(this.getId()).stream().findFirst().orElse(null);
	}

	@Transient
	public String getDestinationCity() {
		if (!this.legRepository().hasLegs(this.getId()))
			return null;

		return this.legRepository().findDestinationCityByFlightId(this.getId()).stream().findFirst().orElse(null);
	}

	@Transient
	public Integer getNumOfLayovers() {
		if (!this.legRepository().hasLegs(this.getId()))
			return null;
		return this.legRepository().findNumberOfLayovers(this.getId()) - 1;
	}


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager manager;

}
