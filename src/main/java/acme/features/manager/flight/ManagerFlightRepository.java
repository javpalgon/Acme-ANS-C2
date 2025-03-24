
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.realms.Manager;

@GuiService
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select p from Flight p where p.manager.id = :managerId")
	Collection<Flight> findFlightsByManagerId(int managerId);

	@Query("select p from Flight p")
	Collection<Flight> findAllFlights();

	@Query("select m from Manager m where m.id = :id")
	Manager findOneManagerById(int id);

	@Query("select m from Manager m where m.userAccount.id = :id")
	Manager findManagerByUserId(int id);

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(int flightId);

}
