
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.assignment.Assignment;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;
import acme.entities.claim.Claim;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.entities.trackinglog.TrackingLog;
import acme.realms.Manager;

@GuiService
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id); //si

	@Query("select p from Flight p where p.manager.id = :managerId")
	Collection<Flight> findFlightsByManagerId(int managerId); // si

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(int flightId); // si

	@Query("select m from Manager m where m.id = :id")
	Manager findOneManagerById(int id); // si

	@Query("select f from Flight f where f.isDraftMode = false")
	Collection<Flight> findPublishedFlights();

	// other students

	@Query("select a from Assignment a where a.leg.id = :legId")
	Collection<Assignment> findAssignmentsByLegId(int legId);

	@Query("select t from TrackingLog t where t.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

	@Query("select l from Booking l where l.flight.id = :flightId")
	Collection<Booking> findBookingsByFlightId(int flightId);

	@Query("select l from BookingRecord l where l.booking.id = :bookingId")
	Collection<BookingRecord> findBookingRecordsByBookingId(int bookingId);

	@Query("select l from ActivityLog l where l.assignment.id = :id")
	Collection<ActivityLog> findActivityLogsByAssigId(int id);

	@Query("select c from Claim c where c.leg.id = :legId")
	Collection<Claim> findClaimsByLegId(int legId);

}
