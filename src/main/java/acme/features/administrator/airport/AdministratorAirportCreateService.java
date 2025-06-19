
package acme.features.administrator.airport;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.airport.OperationalScope;

@GuiService
public class AdministratorAirportCreateService extends AbstractGuiService<Administrator, Airport> {

	@Autowired
	private AdministratorAirportRepository repository;


	@Override
	public void authorise() {
		Boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		if (super.getRequest().hasData("operationalScope", String.class)) {
			String scope = super.getRequest().getData("operationalScope", String.class);
			Set<String> validScopes = Set.of("0", "INTERNATIONAL", "DOMESTIC", "REGIONAL");
			if (!validScopes.contains(scope))
				status = false;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airport airport = new Airport();
		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "IATACode", "operationalScope", "city", "country", "website", "emailAddress", "phoneNumber");
	}

	@Override
	public void validate(final Airport airport) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		if (!super.getBuffer().getErrors().hasErrors("IATACode")) {
			Airport existing = this.repository.findAirportByIATACode(airport.getIATACode());
			boolean isDuplicate = existing != null && existing.getId() != airport.getId();
			super.state(!isDuplicate, "IATACode", "administrator.airport.form.error.duplicate-IATACode");
		}
	}

	@Override
	public void perform(final Airport airport) {
		this.repository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		SelectChoices choices = SelectChoices.from(OperationalScope.class, airport.getOperationalScope());
		Dataset dataset = super.unbindObject(airport, "name", "IATACode", "operationalScope", "city", "country", "website", "emailAddress", "phoneNumber");
		dataset.put("operationalScopes", choices);
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		super.getResponse().addData(dataset);
	}
}
