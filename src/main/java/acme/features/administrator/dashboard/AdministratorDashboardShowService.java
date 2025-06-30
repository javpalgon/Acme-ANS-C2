
package acme.features.administrator.dashboard;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Type;
import acme.entities.airport.OperationalScope;
import acme.forms.AdministratorDashboard;
import acme.forms.Statistics;

@GuiService
public class AdministratorDashboardShowService extends AbstractGuiService<Administrator, AdministratorDashboard> {

	@Autowired
	private AdministratorDashboardRepository repository;


	@Override
	public void authorise() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		Administrator admin = this.repository.findAdministratorByUserAccountId(userAccountId);
		boolean isAuthorised = admin != null;

		super.getResponse().setAuthorised(isAuthorised);
	}

	@Override
	public void load() {

		//1. Total number of airports grouped by their operational scope.
		List<Object[]> results = this.repository.totalAirportsByScope();
		Map<String, Integer> totalAirportsByScope = results.stream().collect(Collectors.toMap(entry -> ((OperationalScope) entry[0]).name(), entry -> ((Long) entry[1]).intValue()));

		//2. Total number of airlines grouped by type.
		List<Object[]> airlineResults = this.repository.findAirlinesGroupedByType();
		Map<String, Integer> totalAirlinesByType = airlineResults.stream().collect(Collectors.toMap(entry -> ((Type) entry[0]).name(), entry -> ((Long) entry[1]).intValue()));

		//3. Ratio of airlines with both email and phone
		Double ratioAirlinesWithEmailAndPhone = this.repository.ratioAirlinesWithEmailAndPhone();

		// 4. Ratios of active and non-active aircrafts
		Double ratioActiveAircrafts = this.repository.findActiveAircraftsRatio();
		Double ratioInactiveAircrafts = 1.0 - ratioActiveAircrafts;

		// 5. Ratio of reviews with a score above 5.00
		Double ratioReviewsAboveFive = this.repository.ratioReviewsAboveFive();

		// 6. Statistics of number of reviews posted weekly in the last 10 weeks
		List<Double> reviewStats = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			Date start;
			Date end;
			Long count = 0L;

			start = MomentHelper.deltaFromCurrentMoment(-i, ChronoUnit.WEEKS);
			end = MomentHelper.deltaFromCurrentMoment(-i + 1L, ChronoUnit.WEEKS);
			count = this.repository.countReviewsBetween(start, end);

			reviewStats.add(count.doubleValue());
		}
		Statistics reviewStatsLast10Weeks = new Statistics();
		reviewStatsLast10Weeks.setData(reviewStats);

		AdministratorDashboard dashboard = new AdministratorDashboard();
		dashboard.setTotalAirportsByScope(totalAirportsByScope);
		dashboard.setTotalAirlinesByType(totalAirlinesByType);
		dashboard.setRatioAirlinesWithEmailAndPhone(ratioAirlinesWithEmailAndPhone);
		dashboard.setRatioActiveAircrafts(ratioActiveAircrafts);
		dashboard.setRatioInactiveAircrafts(ratioInactiveAircrafts);
		dashboard.setRatioReviewsAboveFive(ratioReviewsAboveFive);
		dashboard.setReviewStatsLast10Weeks(reviewStatsLast10Weeks);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final AdministratorDashboard object) {
		assert object != null;

		Dataset dataset = super.unbindObject(object, "totalAirportsByScope", "totalAirlinesByType", "ratioAirlinesWithEmailAndPhone", "ratioActiveAircrafts", "ratioInactiveAircrafts", "ratioReviewsAboveFive", "reviewStatsLast10Weeks");

		super.getResponse().addData(dataset);
	}
	/*
	 * // Total number of airports grouped by operational scope
	 * Map<String, Integer> totalAirportsByScope;
	 * 
	 * // Number of airlines grouped by type
	 * Map<String, Integer> totalAirlinesByType;
	 * 
	 * // Ratio of airlines with both email and phone
	 * Double ratioAirlinesWithEmailAndPhone;
	 * 
	 * // Ratios of active and non-active aircrafts
	 * Double ratioActiveAircrafts;
	 * Double ratioInactiveAircrafts;
	 * 
	 * // Ratio of reviews with score > 5.00
	 * Double ratioReviewsAboveFive;
	 * 
	 * // Review statistics over the last 10 weeks
	 * Statistics reviewStatsLast10Weeks;
	 */

}
