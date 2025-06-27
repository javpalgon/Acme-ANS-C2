
package acme.features.authenticated.administrator.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;

@Repository
public interface AdministratorBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.isDraftMode = false")
	Collection<Booking> findPublishedBookings();

	@Query("select b from Booking b where b.id =:bookingId")
	Booking findBookingById(int bookingId);

	@Query("select br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBooking(int bookingId);

}
