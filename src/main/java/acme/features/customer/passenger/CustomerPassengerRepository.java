
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
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

}
