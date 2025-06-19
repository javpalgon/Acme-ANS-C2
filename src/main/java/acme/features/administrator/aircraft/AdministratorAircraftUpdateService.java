
package acme.features.administrator.aircraft;

import java.util.Collection;
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
public class AdministratorAircraftUpdateService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		Integer id;
		Collection<Aircraft> aircrafts = this.repository.findAllAircrafts();
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		if (status)
			if (super.getRequest().hasData("id")) {
				id = super.getRequest().getData("id", Integer.class);
				if (id != null && aircrafts.stream().anyMatch(x -> id.equals(x.getId()))) {
					if (super.getRequest().hasData("aircraftStatus", String.class)) {
						String statusAir = super.getRequest().getData("aircraftStatus", String.class);
						Set<String> validStatuses = Set.of("0", "ACTIVE", "UNDER_MAINTENANCE");
						if (!validStatuses.contains(statusAir))
							super.getResponse().setAuthorised(false);
						else
							super.getResponse().setAuthorised(status);
					}
					if (super.getRequest().hasData("airline", String.class)) {
						String iataCode = super.getRequest().getData("airline", String.class);
						Set<String> validIATACodes = this.repository.findAllAirlines().stream().map(Airline::getIATACode).collect(Collectors.toSet());

						if (!validIATACodes.contains(iataCode))
							status = false;
					}
				} else
					status = false;
			} else
				status = false;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		int id;

		id = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "regitrationNumber", "capacity", "cargoWeight", "details", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
		Collection<Airline> airlines = this.repository.findAllAirlines();
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
		if (aircraft.getAirline() != null) {
			dataset.put("airlines", SelectChoices.from(this.repository.findAllAirlines(), "IATACode", aircraft.getAirline()));
			dataset.put("name", aircraft.getAirline().getName());
			dataset.put("website", aircraft.getAirline().getWebsite());
		} else
			dataset.put("airlines", SelectChoices.from(this.repository.findAllAirlines(), "IATACode", null));
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		super.getResponse().addData(dataset);
	}
}
