
package acme.features.administrator.airline;

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
public class AdministratorAirlineCreateService extends AbstractGuiService<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		if (status && super.getRequest().hasData("type", String.class)) {
			String type = super.getRequest().getData("type", String.class);
			Set<String> validTypes = Set.of("0", "LUXURY", "STANDARD", "LOWCOS");
			if (!validTypes.contains(type))
				status = false;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airline airline;
		airline = new Airline();
		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "IATACode", "website", "type", "foundationMoment", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airline airline) {
		boolean confirmation;
		List<Airline> airlines = this.repository.findAllAirlines().stream().toList();
		if (airline.getIATACode() != null)
			super.state(!airlines.stream().anyMatch(x -> x.getIATACode().equals(airline.getIATACode())), "IATACode", "administrator.airline.form.error.IATA-not-unique");
		confirmation = super.getRequest().getData("confirmation", boolean.class);
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
