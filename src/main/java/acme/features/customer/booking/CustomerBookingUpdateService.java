
package acme.features.customer.booking;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.Travelclass;
import acme.realms.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

	private static final Logger			logger	= LoggerFactory.getLogger(CustomerBookingShowService.class);

	@Autowired
	protected CustomerBookingRepository	repository;


	@Override
	public void authorise() {
		Booking booking;
		int id;
		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		final int userAccountId = super.getRequest().getPrincipal().getAccountId();
		final int customerId = booking.getCustomer().getUserAccount().getId();
		super.getResponse().setAuthorised(userAccountId == customerId);
	}

	@Override
	public void load() {
		Booking booking;
		int id;
		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking object) {
		assert object != null;
		super.bindObject(object, "locatorCode", "purchaseMoment", "travelClass", "lastNibble");
	}

	@Override
	public void validate(final Booking object) {
		assert object != null;

		// 1. Debe estar en borrador
		super.state(object.getIsDraftMode(), "isDraftMode", "customer.booking.form.error.not-draft");

		// 2. LocatorCode: no vacío
		super.state(!StringHelper.isBlank(object.getLocatorCode()), "locatorCode", "customer.booking.form.error.blank-locatorCode");

		// 3. LocatorCode: patrón y longitud
		boolean validLocatorCode = object.getLocatorCode().matches("^[A-Z0-9]{6,8}$");
		super.state(validLocatorCode, "locatorCode", "customer.booking.form.error.invalid-locatorCode");

		// 4. LocatorCode: unicidad (excluyendo el propio id)
		boolean isDuplicate = this.repository.existsByLocatorCode(object.getLocatorCode(), object.getId());
		super.state(!isDuplicate, "locatorCode", "customer.booking.form.error.duplicate-locatorCode");

		// 5. PurchaseMoment: debe estar en el pasado
		boolean isPast = object.getPurchaseMoment() != null && MomentHelper.isBefore(object.getPurchaseMoment(), MomentHelper.getCurrentMoment());
		super.state(isPast, "purchaseMoment", "customer.booking.form.error.invalid-purchaseMoment");

		// 6. TravelClass: obligatorio
		super.state(object.getTravelClass() != null, "travelClass", "customer.booking.form.error.travelClass-required");

		// 7. LastNibble: si no es null, debe tener exactamente 4 dígitos
		if (!StringHelper.isBlank(object.getLastNibble())) {
			boolean validNibble = object.getLastNibble().matches("^[0-9]{4}$");
			super.state(validNibble, "lastNibble", "customer.booking.form.error.invalid-lastNibble");
		}
	}

	@Override
	public void perform(final Booking object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final Booking object) {
		assert object != null;
		SelectChoices choices;

		choices = SelectChoices.from(Travelclass.class, object.getTravelClass());
		Dataset dataset = super.unbindObject(object, "locatorCode", "purchaseMoment", "travelClass", "lastNibble");

		List<String> passengers = this.repository.findPassengersByBooking(object.getId()).stream().map(p -> p.getFullName()).toList();
		Money totalPrice = object.getPrice();

		dataset.put("totalPrice", totalPrice);
		dataset.put("travelClasses", choices);
		dataset.put("hasPassengers", !passengers.isEmpty());
		dataset.put("passengers", passengers);

		super.getResponse().addData(dataset);
	}

}
