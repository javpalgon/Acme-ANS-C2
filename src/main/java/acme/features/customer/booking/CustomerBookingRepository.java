
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.flight.Flight;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id =:bookingId")
	Booking findBookingById(int bookingId);

	@Query("select b from Booking b where b.customer.id =:customerId")
	Collection<Booking> findBookingsByCustomerId(int customerId);

	@Query("select br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBooking(int bookingId);

	@Query("select count(b) > 0 from Booking b where b.locatorCode = :locatorCode and b.id != :id")
	boolean existsByLocatorCode(String locatorCode, int id);

	@Query("select c from Customer c where c.userAccount.id = :accountId")
	Customer findCustomerByUserAccountId(int accountId);

	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Booking findBookingByLocatorCode(String locatorCode);

	@Query("SELECT f FROM Flight f WHERE f.isDraftMode = false")
	Collection<Flight> findPublishedFlights();

}
