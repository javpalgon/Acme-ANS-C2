
package acme.features.administrator.airport;

import java.util.Collection;
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
public class AdministratorAirportUpdateService extends AbstractGuiService<Administrator, Airport> {

	@Autowired
	private AdministratorAirportRepository repository;


	@Override
	public void authorise() {
		boolean status = false; // Valor por defecto: no autorizado
		Collection<Airport> airports = this.repository.findAllAirports();
		// 1. Comprobar si el usuario tiene rol de Administrator
		boolean isAdmin = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		if (isAdmin)
			// 2. Comprobar si hay un parámetro "id" en la solicitud
			if (super.getRequest().hasData("id")) {
				Integer id = super.getRequest().getData("id", Integer.class);

				// 3. Validar que el id no sea null
				if (id != null && airports.stream().anyMatch(x -> id.equals(x.getId()))) {
					status = true;
					// 5. Verificar si se ha enviado el campo "operationalScope"
					if (super.getRequest().hasData("operationalScope", String.class)) {
						String scope = super.getRequest().getData("operationalScope", String.class);
						Set<String> validScopes = Set.of("0", "INTERNATIONAL", "DOMESTIC", "REGIONAL");

						// 6. Comprobar si el scope es válido
						if (!validScopes.contains(scope))
							status = false; // Todo correcto
					}
				}
			}

		// 7. Establecer autorización
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airport airport;
		int id;

		id = super.getRequest().getData("id", int.class);
		airport = this.repository.findAirportById(id);

		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "IATACode", "operationalScope", "city", "country", "website", "emailAddress", "phoneNumber");
	}

	@Override
	public void validate(final Airport airport) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
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
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(OperationalScope.class, airport.getOperationalScope());
		dataset = super.unbindObject(airport, "name", "IATACode", "operationalScope", "city", "country", "website", "emailAddress", "phoneNumber");
		dataset.put("operationalScopes", choices);
		dataset.put("confirmation", false);
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}
}
