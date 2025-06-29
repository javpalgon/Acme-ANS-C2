
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRepository;

@Validator
public class BookingValidator extends AbstractValidator<ValidBooking, Booking> {

	@Autowired
	private BookingRepository repository;


	@Override
	protected void initialise(final ValidBooking annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Booking booking, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result = true;

		if (booking == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;

		} else {

			// 1. Locator code must be unique
			if (booking.getLocatorCode() != null) {
				Booking existingBooking = this.repository.getBookingByLocatorCode(booking.getLocatorCode());
				boolean uniqueLocatorCode = existingBooking == null || existingBooking.equals(booking);
				super.state(context, uniqueLocatorCode, "locatorCode", "acme.validation.booking.uniqueLocatorCode.message");
			}

			// 2. Flight must be published and in the future
			if (booking.getFlight() != null) {
				boolean isPublished = !booking.getFlight().getIsDraftMode();
				boolean isFuture = booking.getFlight().getDeparture() != null && MomentHelper.isAfterOrEqual(booking.getFlight().getDeparture(), MomentHelper.getCurrentMoment());
				super.state(context, isPublished, "flight", "acme.validation.booking.flight.notPublished");
				super.state(context, isFuture, "flight", "acme.validation.booking.flight.notFuture");
			}

			// 3. If not draft mode, lastNibble must be present and valid
			if (!booking.getIsDraftMode()) {
				boolean hasLastNibble = booking.getLastNibble() != null && !booking.getLastNibble().isBlank();
				super.state(context, hasLastNibble, "lastNibble", "acme.validation.booking.lastCardNibble.message");

				// 4. Booking must have passengers
				boolean hasPassengers = this.repository.countNumberOfPassengers(booking.getId()).compareTo(0L) > 0;
				super.state(context, hasPassengers, "*", "acme.validation.booking.passengers.message");
			}

			// 5. TravelClass must not be null
			super.state(context, booking.getTravelClass() != null, "travelClass", "acme.validation.booking.travelClass.required");

			// 6. Flight must not be null
			super.state(context, booking.getFlight() != null, "flight", "acme.validation.booking.flight.required");
		}
		return !super.hasErrors(context);
	}
}
