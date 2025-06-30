
package acme.forms;

import java.util.Map;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministratorDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	// Total number of airports grouped by operational scope
	Map<String, Integer>		totalAirportsByScope;

	// Number of airlines grouped by type
	Map<String, Integer>		totalAirlinesByType;

	// Ratio of airlines with both email and phone
	Double						ratioAirlinesWithEmailAndPhone;

	// Ratios of active and non-active aircrafts
	Double						ratioActiveAircrafts;
	Double						ratioInactiveAircrafts;

	// Ratio of reviews with score > 5.00
	Double						ratioReviewsAboveFive;

	// Review statistics over the last 10 weeks
	Statistics					reviewStatsLast10Weeks;
}
