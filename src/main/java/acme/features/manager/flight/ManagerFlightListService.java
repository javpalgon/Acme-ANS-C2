
package acme.features.manager.flight;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Principal;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightListService extends AbstractGuiService<Manager, Flight> {

	private static final Logger		logger	= LoggerFactory.getLogger(ManagerFlightListService.class);

	@Autowired
	private ManagerFlightRepository	repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Flight> flights = new ArrayList<>();
		Principal principal;
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		flights = this.repository.findFlightsByManagerId(managerId);

		//	int userAccountId = super.getRequest().getPrincipal().getAccountId();

		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight object) {
		assert object != null;

		Dataset dataset;
		dataset = super.unbindObject(object, "tag", "cost", "description", "requiresSelfTransfer", "description");
		super.getResponse().addData(dataset);
	}
}
