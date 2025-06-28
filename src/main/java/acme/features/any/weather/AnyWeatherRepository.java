
package acme.features.any.weather;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.entities.weather.Weather;

@Repository
public interface AnyWeatherRepository extends AbstractRepository {

	@Query("SELECT w FROM Weather w WHERE w.city = :city")
	List<Weather> findWeathersByCity(@Param("city") String city);

	@Query("SELECT l FROM Leg l WHERE l.departure BETWEEN :start AND :end")
	List<Leg> findLegsBetween(@Param("start") Date start, @Param("end") Date end);

	@Query("select w from Weather w")
	Page<Weather> fetch(final PageRequest pageRequest);

	@Query("select w from Weather w")
	List<Weather> findAllWeathers();

	@Query("select f from Weather f where f.id = :id")
	Weather findWeatherById(int id);

	@Query("""
		    SELECT DISTINCT f FROM Flight f
		    WHERE f.isDraftMode = false
		    AND EXISTS (
		        SELECT l FROM Leg l
		        WHERE l.flight = f
		        AND l.departure >= :startDate
		    )
		""")
	List<Flight> findAllPublishedFlightsLastMonth(@Param("startDate") Date startDate);

	@Query("""
		    SELECT w FROM Weather w
		    WHERE w.isBadWeather = true
		    AND w.timestamp >= :startDate
		""")
	List<Weather> findAllBadWeathersLastMonth(@Param("startDate") Date startDate);

}
