
package acme.entities.weather;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Weather extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Optional
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				city;

	@Optional
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				timestamp;

	@Optional
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				weatherMain;

	@Optional
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				weatherDescription;

	@Optional
	@ValidNumber(min = 0, max = 10000)
	@Automapped

	private Integer				visibility;
	@Optional
	@ValidNumber(min = 0, max = 300)
	@Automapped
	private Double				windSpeed;

	@Optional
	@Automapped
	private Boolean				isBadWeather;
}
