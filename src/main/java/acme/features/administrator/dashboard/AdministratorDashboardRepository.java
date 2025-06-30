
package acme.features.administrator.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.Administrator;
import acme.client.repositories.AbstractRepository;

@Repository
public interface AdministratorDashboardRepository extends AbstractRepository {

	// 1. Total number of airports grouped by their operational scope
	@Query("select a.operationalScope, count(a) from Airport a group by a.operationalScope")
	List<Object[]> totalAirportsByScope();

	// 2. Number of airlines grouped by their type
	@Query("select a.type, count(a) from Airline a group by a.type")
	List<Object[]> findAirlinesGroupedByType();

	// 3. Ratio of airlines with both email and phone
	@Query("select COALESCE(1.0 * count(a) / NULLIF((select count(r) from Airline r),0),0.0) from Airline a where (a.phoneNumber != '' and a.email != '')")
	Double ratioAirlinesWithEmailAndPhone();

	// 4. Ratios of active and non-active aircrafts
	@Query("select COALESCE(1.0 * count(a) / NULLIF((select count(r) from Aircraft r),0),0.0) from Aircraft a where a.aircraftStatus = 0")
	Double findActiveAircraftsRatio();

	// 5. Ratio of reviews with a score above 5.00
	@Query("select COALESCE(1.0 * count(a) / NULLIF((select count(r) from Review r),0),0.0) from Review a where a.score > 5")
	Double ratioReviewsAboveFive();

	// 6. Statistics of number of reviews posted weekly in the last 10 weeks
	@Query("select count(r) from Review r where r.postedAt between :start and :end")
	Long countReviewsBetween(Date start, Date end);

	@Query("select a from Administrator a where a.userAccount.id = :userAccountId")
	Administrator findAdministratorByUserAccountId(int userAccountId);

}
