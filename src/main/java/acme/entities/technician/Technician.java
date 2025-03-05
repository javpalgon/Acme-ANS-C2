
package acme.entities.technician;

import javax.persistence.Entity;
import javax.validation.constraints.Size;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Technician extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Automapped
	//Crear Validación
	private String				licenseNumber;

	@Mandatory
	@Automapped
	//Crear Validación
	private String				phoneNumber;

	@Mandatory
	@Size(min = 0, max = 50)
	@Automapped
	private String				specialisation;

	@Mandatory
	@Automapped
	private Boolean				passedHealthTet;

	@Mandatory
	@Automapped
	private Integer				yearsOfExperience;

	@Optional
	@Size(min = 0, max = 255)
	@Automapped
	private String				certifications;

}
