
package acme.entities.booking;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.datatypes.Money;
import acme.client.repositories.AbstractRepository;
import acme.entities.flight.Flight;

@Repository
public interface BookingRepository extends AbstractRepository {

	@Query("SELECT f.cost FROM Flight f WHERE f.id = :id")
	Money findPriceByFlightId(int id);

	@Query("select count(br.passenger) from BookingRecord br where br.booking.id = :bookingId")
	Integer findNumberPassengersByBooking(int bookingId);

	//Valid Booking methods
	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Booking getBookingByLocatorCode(String locatorCode);

	@Query("select count(br) from BookingRecord br where br.booking.id = :bookingId")
	Long countNumberOfPassengers(int bookingId);

	@Query("select f from Flight f where f.id = :flightId")
	Flight findFlightById(int flightId);

}
