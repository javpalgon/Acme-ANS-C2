
package acme.entities.booking;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.datatypes.Money;
import acme.client.repositories.AbstractRepository;

@Repository
public interface BookingRepository extends AbstractRepository {

	@Query("select b.flight.cost from Booking b where b.id = :bookingId")
	Money findPriceByBookingId(int bookingId);

	@Query("select count(br.passenger) from BookingRecord br where br.booking.id = :bookingId")
	Integer findNumberPassengersByBooking(int bookingId);

}
