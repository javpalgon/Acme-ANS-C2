
package acme.entities.booking;

import java.beans.Transient;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.flight.Flight;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "customer_id"), @Index(columnList = "locatorCode")
})
public class Booking extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@NotBlank
	@ValidString(max = 8, min = 6, pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Valid
	@Automapped
	private Travelclass			travelClass;

	@Optional
	@ValidString(min = 4, max = 4, pattern = "[0-9]{4}")
	@Automapped
	private String				lastNibble;

	@Mandatory
	@Automapped
	private Boolean				isDraftMode;


	@Transient
	public Money getPrice() {
		Money res;
		Integer totalPassengers;
		BookingRepository bookingRepository = SpringHelper.getBean(BookingRepository.class);

		res = bookingRepository.findPriceByFlightId(this.flight.getId());
		totalPassengers = bookingRepository.findNumberPassengersByBooking(this.getId());
		Double totalPrice = res.getAmount() * totalPassengers;

		res.setAmount(totalPrice);
		return res;
	}


	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Flight		flight;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Customer	customer;

}
