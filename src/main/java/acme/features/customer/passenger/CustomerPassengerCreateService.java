
package acme.features.customer.passenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;
import acme.entities.passenger.Passenger;
import acme.features.customer.booking.CustomerBookingListService;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerCreateService extends AbstractGuiService<Customer, Passenger> {

	private static final Logger				logger	= LoggerFactory.getLogger(CustomerBookingListService.class);

	@Autowired
	protected CustomerPassengerRepository	repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Passenger passenger = new Passenger();
		passenger.setIsDraftMode(true);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger object) {
		assert object != null;
		super.bindObject(object, "fullName", "passport", "email", "birth", "specialNeeds");
	}

	@Override
	public void validate(final Passenger object) {
		assert object != null;

		// Solo se puede modificar si está en modo borrador
		super.state(object.getIsDraftMode(), "*", "customer.passenger.form.error.not-draft");

		// Validación de fullName
		super.state(object.getFullName() != null && !object.getFullName().trim().isEmpty(), "fullName", "customer.passenger.form.error.fullName.required");
		super.state(object.getFullName() != null && object.getFullName().length() <= 255, "fullName", "customer.passenger.form.error.fullName.length");

		// Validación de email
		super.state(object.getEmail() != null && object.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"), "email", "customer.passenger.form.error.email.invalid");

		// Validación de passport
		super.state(object.getPassport() != null && object.getPassport().matches("^[A-Z0-9]{6,9}$"), "passport", "customer.passenger.form.error.passport.invalid");
		Passenger existing = this.repository.findPassengerByPassport(object.getPassport());
		if (existing != null)
			super.state(false, "passport", "customer.passenger.form.error.duplicate-passport");

		// Validación de birth
		boolean isPast = object.getBirth() != null && MomentHelper.isBefore(object.getBirth(), MomentHelper.getCurrentMoment());
		super.state(isPast, "birth", "customer.passenger.form.error.birth.past");

		// Validación de specialNeeds
		if (object.getSpecialNeeds() != null)
			super.state(object.getSpecialNeeds().length() <= 50, "specialNeeds", "customer.passenger.form.error.specialNeeds.length");

	}

	@Override
	public void perform(final Passenger object) {
		assert object != null;

		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		Passenger existing = this.repository.findPassengerByPassport(object.getPassport());

		if (existing != null) {
			// Ya existe: asociamos a esta booking si no lo está ya
			boolean alreadyLinked = this.repository.existsRecordByBookingIdAndPassengerId(bookingId, existing.getId());
			if (!alreadyLinked) {
				BookingRecord record = new BookingRecord();
				record.setBooking(booking);
				record.setPassenger(existing);
				this.repository.save(record);
			}
		} else {
			// No existe: lo creamos y lo asociamos
			this.repository.save(object);

			BookingRecord record = new BookingRecord();
			record.setBooking(booking);
			record.setPassenger(object);
			this.repository.save(record);
		}
	}

	@Override
	public void unbind(final Passenger object) {
		Dataset dataset = super.unbindObject(object, "fullName", "passport", "email", "birth", "specialNeeds");
		dataset.put("isDraftMode", object.getIsDraftMode());
		int bookingId = super.getRequest().getData("bookingId", int.class);
		dataset.put("bookingId", bookingId);

		super.getResponse().addData(dataset);
	}
}
