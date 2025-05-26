
package acme.features.administrator.airline;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.Type;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Integer id;
		Collection<Airline> airlines = this.repository.findAllAirlines();
		Boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		if (super.getRequest().hasData("id")) {
			id = super.getRequest().getData("id", Integer.class);
			if (id != null && airlines.stream().anyMatch(x -> id.equals(x.getId())))
				super.getResponse().setAuthorised(status);
		} else
			super.getResponse().setAuthorised(false);
		if (status && super.getRequest().hasData("type", String.class)) {
			String type = super.getRequest().getData("type", String.class);
			Set<String> validTypes = Set.of("0", "LUXURY", "STANDARD", "LOWCOST");
			if (!validTypes.contains(type))
				status = false;
		}
	}

	@Override
	public void load() {
		Airline airline;
		int id;

		id = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(id);

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "IATACode", "website", "type", "foundationMoment", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airline airline) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		List<Airline> airlines = this.repository.findAllAirlines().stream().toList();
		if (airline.getIATACode() != null)
			super.state(!airlines.stream().anyMatch(x -> x.getIATACode().equals(airline.getIATACode())), "IATACode", "administrator.airline.form.error.IATA-not-unique");
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airline) {
		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(Type.class, airline.getType());
		dataset = super.unbindObject(airline, "name", "IATACode", "website", "type", "foundationMoment", "email", "phoneNumber");
		dataset.put("Type", choices);
		dataset.put("confirmation", false);
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}

}
