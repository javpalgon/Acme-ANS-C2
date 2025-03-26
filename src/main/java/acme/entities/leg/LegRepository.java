
package acme.entities.leg;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findAllLegsByFlightId(@Param("flightId") Integer flightId);

	@Query("select l.departure from Leg l where l.flight.id = :flightId order by l.departure")
	Collection<Date> findDepartureByFlightId(@Param("flightId") Integer flightId);

	@Query("select l.arrival from Leg l where l.flight.id = :flightId order by l.departure desc")
	Collection<Date> findArrivalByFlightId(@Param("flightId") Integer flightId);

	@Query("select l.departureAirport.city from Leg l where l.flight.id = :flightId order by l.departure")
	Collection<String> findOriginCityByFlightId(@Param("flightId") Integer flightId);

	@Query("select l.arrivalAirport.city from Leg l where l.flight.id = :flightId order by l.departure")
	Collection<String> findDestinationCityByFlightId(@Param("flightId") Integer flightId);

	@Query("select count(l) from Leg l where l.flight.id = :flightId")
	Integer findNumberOfLayovers(Integer flightId);

}
