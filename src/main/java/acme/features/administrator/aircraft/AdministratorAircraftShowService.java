
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
		SelectChoices choices;
		choices = SelectChoices.from(AircraftStatus.class, aircraft.getAircraftStatus());

		Dataset dataset = super.unbindObject(aircraft, "model", "regitrationNumber", "capacity", "cargoWeight", "aircraftStatus", "details");
		dataset.put("aircraftStatus", choices);
		dataset.put("name", aircraft.getAirline().getName());
		dataset.put("IATACode", aircraft.getAirline().getIATACode());
		dataset.put("website", aircraft.getAirline().getWebsite());

		super.getResponse().addData(dataset);
	}
}
