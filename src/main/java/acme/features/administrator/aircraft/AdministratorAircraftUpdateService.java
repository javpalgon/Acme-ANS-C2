
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
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		if (status)
			if (super.getRequest().hasData("aircraftStatus", String.class)) {
				String statusAir = super.getRequest().getData("aircraftStatus", String.class);
				Set<String> validStatuses = Set.of("0", "ACTIVE", "UNDER_MAINTENANCE");
				if (!validStatuses.contains(statusAir))
					status = false;
			}
		Airline airline = this.repository.getAirlineById(super.getRequest().getData("airline", int.class));

		if (airline != null) {
			String iataCode = airline.getIATACode();
			Set<String> validIATACodes = this.repository.findAllAirlines().stream().map(Airline::getIATACode).collect(Collectors.toSet());

			if (!validIATACodes.contains(iataCode) && !iataCode.equals("0"))
				status = false;
		}

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
		int airlineId = super.getRequest().getData("airline", int.class);
		Airline airline = this.repository.getAirlineById(airlineId);

		super.bindObject(aircraft, "model", "regitrationNumber", "capacity", "cargoWeight", "aircraftStatus", "details");
		aircraft.setAirline(airline);
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
		Dataset dataset = super.unbindObject(aircraft, "model", "regitrationNumber", "capacity", "cargoWeight", "aircraftStatus", "details");

		SelectChoices statusChoices;
		statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getAircraftStatus());
		SelectChoices selectedAirline = new SelectChoices();
		Collection<Airline> airlines = this.repository.findAllAirlines();
		selectedAirline = SelectChoices.from(airlines, "IATACode", aircraft.getAirline());

		dataset.put("airlines", selectedAirline);
		dataset.put("airline", selectedAirline.getSelected().getKey());
		dataset.put("aircraftStatus", statusChoices);
		dataset.put("airlines", SelectChoices.from(this.repository.findAllAirlines(), "IATACode", aircraft.getAirline()));
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		super.getResponse().addData(dataset);
	}
}
