
package acme.forms;

import java.util.Map;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDashboard extends AbstractForm {

	private static final long		serialVersionUID	= 1L;

	private String[]				lastFiveDestinations;

	private Map<String, Double>		totalMoneySpentLastYearByCurrency;

	private Map<String, Integer>	bookingsPerTravelClass;

	private Map<String, Statistics>	bookingCostStatsLastFiveYears;

	private Statistics				passengersStatistics;
}
