
package acme.features.administrator.airport;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;

@GuiService
public class AdministratorAirportListService extends AbstractGuiService<Administrator, Airport> {

	@Autowired
	private AdministratorAirportRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Airport> airports = this.repository.findAllAirports();
		super.getBuffer().addData(airports);
	}

	@Override
	public void unbind(final Airport object) {
		assert object != null;

		Dataset dataset = super.unbindObject(object, "name", "IATACode", "operationalScope", "city", "country", "website", "emailAddress", "phoneNumber");

		super.getResponse().addData(dataset);
	}

}
