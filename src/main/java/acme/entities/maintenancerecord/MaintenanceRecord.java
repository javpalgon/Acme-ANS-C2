
package acme.entities.maintenancerecord;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.entities.aircraft.Aircraft;
import acme.entities.technician.Technician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MaintenanceRecord extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				maintenanceTimestamp;

	@Mandatory
	@Valid
	@Automapped
	private MaintenanceStatus	maintenanceStatus;

	//TODO: Add validation nextIns > maintenanceTimstamp
	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				nextInspectionDate;

	@Mandatory
	@ValidMoney(min = 0.0)
	@Automapped
	private Money				estimatedCost;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				notes;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Aircraft			aircraft;

	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Technician			technician;

}
