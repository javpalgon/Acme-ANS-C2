
package acme.features.manager.dashboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.forms.ManagerDashboard;
import acme.forms.Statistics;
import acme.realms.Manager;

@GuiService
public class ManagerDashboardShowService extends AbstractGuiService<Manager, ManagerDashboard> {

	@Autowired
	private ManagerDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Integer yearsToRetire = this.repository.findYearsToRetire(managerId);
		Integer positionInRanking = this.repository.findPositionInRanking(managerId);
		Integer onTimeLegs = this.repository.countOnTimeLegs(managerId);
		Integer delayedLegs = this.repository.countDelayedLegs(managerId);

		String mostPopularAirport = this.repository.findMostPopularAirports(managerId, PageRequest.of(0, 1));
		String leastPopularAirport = this.repository.findLeastPopularAirports(managerId, PageRequest.of(0, 1));

		List<Object[]> rawLegsByStatus = this.repository.findLegsGroupedByStatus(managerId);
		Map<String, Integer> legsByStatus = new HashMap<>();
		for (Object[] row : rawLegsByStatus) {
			String status = row[0].toString();
			Long count = (Long) row[1];
			Integer countvalue = count.intValue();
			legsByStatus.put(status, countvalue);
		}

		StringBuilder summaryBuilder = new StringBuilder();
		for (Map.Entry<String, Integer> entry : legsByStatus.entrySet())
			summaryBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
		String summary = summaryBuilder.length() > 0 ? summaryBuilder.substring(0, summaryBuilder.length() - 2) : "";

		Map<String, Statistics> mapStatisticsCost = new HashMap<>();
		Collection<String> usedCurrencies = this.repository.findCurrencies(managerId);
		for (String currency : usedCurrencies) {
			Statistics costStatistics = new Statistics();
			List<Double> moneyAmount = this.repository.findCostsByCurrency(managerId, currency);
			costStatistics.setData(moneyAmount);
			mapStatisticsCost.put(currency, costStatistics);
		}

		ManagerDashboard dashboard = new ManagerDashboard();
		if (delayedLegs > 0)
			dashboard.setDelayedRatio(onTimeLegs / delayedLegs);
		else
			dashboard.setDelayedRatio(0);

		dashboard.setNumberOfLegsPerStatusSummary(summary);
		dashboard.setYearsToRetire(yearsToRetire);
		dashboard.setManagerRanking(positionInRanking);
		dashboard.setLessPopularAirport(leastPopularAirport);
		dashboard.setMostPopularAirport(mostPopularAirport);
		dashboard.setPriceStatistics(mapStatisticsCost);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final ManagerDashboard dashboard) {
		Dataset dataset = super.unbindObject(dashboard, "yearsToRetire", "managerRanking", "priceStatistics", "delayedRatio", "lessPopularAirport", "mostPopularAirport", "numberOfLegsPerStatusSummary");

		super.getResponse().addData(dataset);
	}

}
