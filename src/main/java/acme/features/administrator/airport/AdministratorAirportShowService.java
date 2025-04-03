
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.airport.OperationalScope;

@GuiService
public class AdministratorAirportShowService extends AbstractGuiService<Administrator, Airport> {

	@Autowired
	private AdministratorAirportRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Airport airport = this.repository.findAirportById(id);
		super.getBuffer().addData(airport);
	}

	@Override
	public void unbind(final Airport object) {
		assert object != null;
		SelectChoices choices;
		choices = SelectChoices.from(OperationalScope.class, object.getOperationalScope());

		Dataset dataset = super.unbindObject(object, "name", "IATACode", "operationalScope", "city", "country", "website", "emailAddress", "phoneNumber");
		dataset.put("operationalScopes", choices);

		super.getResponse().addData(dataset);

	}
}
