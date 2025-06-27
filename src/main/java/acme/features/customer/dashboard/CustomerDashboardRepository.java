
package acme.features.customer.dashboard;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@Repository
public interface CustomerDashboardRepository extends AbstractRepository {

	@Query("select b.flight from Booking b where b.customer.id = :customerId and b.isDraftMode = false order by b.purchaseMoment desc")
	Collection<Flight> findLastFiveDestinations(int customerId);

	@Query("select b from Booking b where b.customer.id =:customerId and b.isDraftMode = false")
	Collection<Booking> findBookingsByCustomerId(int customerId);

	@Query("select b.travelClass, count(b) from Booking b where b.customer.id = :customerId and b.isDraftMode = false group by b.travelClass")
	List<Object[]> findNumberOfBookingsGroupedByTravelClass(int customerId);

	@Query("select count(br) from BookingRecord br where br.booking.customer.id = :customerId and br.booking.isDraftMode = false group by br.booking.id")
	Collection<Long> findBookingRecordCountsPerBooking(int customerId);

	@Query("select c from Customer c where c.userAccount.id = :id")
	Customer findCustomerByUserAccountId(int id);

}
