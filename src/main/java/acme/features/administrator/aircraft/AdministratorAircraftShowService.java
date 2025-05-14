
package acme.features.administrator.aircraft;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;

@GuiService
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Aircraft aircraft = this.repository.findAircraftById(id);
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;
		SelectChoices statusChoices;
		Dataset dataset = super.unbindObject(aircraft, "model", "regitrationNumber", "capacity", "cargoWeight", "aircraftStatus", "details", "enable");
		statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getAircraftStatus());
		SelectChoices IATACodeChoices = SelectChoices.from(this.repository.findAllAirlines(), "IATACode", aircraft.getAirline());
		dataset.put("name", aircraft.getAirline().getName());
		dataset.put("website", aircraft.getAirline().getWebsite());
		dataset.put("aircraftStatus", statusChoices);
		dataset.put("airline", IATACodeChoices.getSelected().getKey());
		dataset.put("airlines", IATACodeChoices);

		super.getResponse().addData(dataset);
	}
}
