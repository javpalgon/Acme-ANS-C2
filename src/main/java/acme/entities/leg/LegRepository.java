
package acme.entities.leg;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.flight.id = :flightId order by l.departure")
	Leg findFirstLegByFlightId(Integer flightId);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.departure desc")
	Leg findLastLegByFlightId(Integer flightId);

	@Query("select count(l) from Leg l where l.flight.id = :flightId")
	Integer numLegsByFlightId(Integer flightId);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.departure")
	List<Leg> findAllLegsByFlightId(Integer flightId);

}
