
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("select br.passenger from BookingRecord br where br.booking.customer.userAccount.id = :accountId")
	Collection<Passenger> findPassengersByCustomerId(int accountId);

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select br.booking.customer.userAccount.id from BookingRecord br where br.passenger.id = :id")
	Collection<Integer> findCustomerUserAccountIdsByPassengerId(int id);

	@Query("SELECT br.passenger FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("select count(p) > 0 from Passenger p where p.passport = :passport and p.id != :id")
	boolean existsByPassport(String passport, int id);

	@Query("select b from Booking b where b.customer.userAccount.id = :accountId and b.isDraftMode = true")
	Collection<Booking> findBookingsInDraftModeByCustomerAccountId(int accountId);

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select p from Passenger p where p.passport = :passport")
	Passenger findPassengerByPassport(String passport);

	@Query("select count(br) > 0 from BookingRecord br where br.booking.id = :bookingId and br.passenger.id = :passengerId")
	boolean existsBookingRecord(int bookingId, int passengerId);

	@Query("select count(br) > 0 from BookingRecord br where br.booking.id = :bookingId and br.passenger.id = :passengerId")
	boolean existsRecordByBookingIdAndPassengerId(int bookingId, int passengerId);

}
