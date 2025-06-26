
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.Travelclass;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	protected CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int flightId;
		Flight flight;

		if (super.getRequest().getMethod().equals("GET"))
			// Mostrar formulario → siempre permitido
			status = true;
		else {
			// Envío del formulario (POST) → validación necesaria
			flightId = super.getRequest().getData("flight", int.class);
			flight = this.repository.findFlightById(flightId);

			if (flightId == 0)
				status = true;
			else
				status = flight != null && !flight.getIsDraftMode() && MomentHelper.isAfterOrEqual(flight.getDeparture(), MomentHelper.getCurrentMoment());
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking = new Booking();

		booking.setLocatorCode("");
		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());
		booking.setTravelClass(null);
		booking.setLastNibble(null);
		booking.setIsDraftMode(true);

		Customer customer = this.repository.findCustomerByUserAccountId(super.getRequest().getPrincipal().getAccountId());
		booking.setCustomer(customer);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking object) {
		assert object != null;
		super.bindObject(object, "locatorCode", "travelClass", "lastNibble", "flight");
	}

	@Override
	public void validate(final Booking object) {
		if (!super.getBuffer().getErrors().hasErrors("locatorCode")) {
			Booking existing = this.repository.findBookingByLocatorCode(object.getLocatorCode());
			super.state(existing == null, "locatorCode", "customer.booking.form.error.duplicated");
		}
	}

	@Override
	public void perform(final Booking object) {
		this.repository.save(object);
	}

	@Override
	public void unbind(final Booking object) {
		Dataset dataset;
		dataset = super.unbindObject(object, "locatorCode", "travelClass", "lastNibble");

		Collection<Flight> flights = this.repository.findPublishedFlights();
		Collection<Flight> availableFlights = flights.stream().filter(f -> MomentHelper.isAfterOrEqual(f.getDeparture(), MomentHelper.getCurrentMoment())).toList();

		SelectChoices flightChoices = SelectChoices.from(availableFlights, "tag", object.getFlight());
		dataset.put("flights", flightChoices);

		SelectChoices choices = SelectChoices.from(Travelclass.class, object.getTravelClass());
		dataset.put("travelClasses", choices);

		super.getResponse().addData(dataset);
	}

}
