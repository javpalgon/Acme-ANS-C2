
package acme.features.administrator.aircraft;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.airline.Airline;

@GuiService
public class AdministratorAircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		if (status) {

			if (super.getRequest().hasData("aircraftStatus", String.class)) {
				String statusAir = super.getRequest().getData("aircraftStatus", String.class);
				Set<String> validStatuses = Set.of("0", "ACTIVE", "UNDER_MAINTENANCE");
				if (!validStatuses.contains(statusAir))
					status = false;
			}

			if (super.getRequest().hasData("airline", String.class)) {
				String iataCode = super.getRequest().getData("airline", String.class);
				Set<String> validIATACodes = this.repository.findAllAirlines().stream().map(Airline::getIATACode).collect(Collectors.toSet());

				if (!validIATACodes.contains(iataCode))
					status = false;
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Aircraft aircraft = new Aircraft();
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "regitrationNumber", "capacity", "cargoWeight", "aircraftStatus", "details", "airline", "enable");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;
		SelectChoices statusChoices;
		statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getAircraftStatus());

		Dataset dataset = super.unbindObject(aircraft, "model", "regitrationNumber", "capacity", "cargoWeight", "aircraftStatus", "details", "enable");
		dataset.put("aircraftStatus", statusChoices);
		//dataset.put("name", SelectChoices.from(this.repository.findAllAirlines(), "name", aircraft.getAirline()));
		dataset.put("airlines", SelectChoices.from(this.repository.findAllAirlines(), "IATACode", aircraft.getAirline()));
		//		dataset.put("website", aircraft.getAirline().getWebsite());
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		super.getResponse().addData(dataset);
	}
}
