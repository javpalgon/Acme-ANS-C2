
package acme.entities.leg;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.flight.id = :flightId")
	List<Leg> findAllLegsByFlightId(@Param("flightId") Integer flightId);

	@Query("select l.departure from Leg l where l.flight.id = :flightId order by l.departure")
	List<Date> findDepartureByFlightId(@Param("flightId") Integer flightId);

	@Query("select l.arrival from Leg l where l.flight.id = :flightId order by l.departure desc")
	List<Date> findArrivalByFlightId(@Param("flightId") Integer flightId);

	@Query("select l.departureAirport.city from Leg l where l.flight.id = :flightId order by l.departure")
	List<String> findOriginCityByFlightId(@Param("flightId") Integer flightId);

	@Query("select l.arrivalAirport.city from Leg l where l.flight.id = :flightId order by l.departure")
	List<String> findDestinationCityByFlightId(@Param("flightId") Integer flightId);

	@Query("select count(l) from Leg l where l.flight.id = :flightId")
	Integer findNumberOfLayovers(Integer flightId);

}
