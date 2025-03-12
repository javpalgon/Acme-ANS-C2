
package acme.forms.manager;

import acme.client.components.basis.AbstractForm;
import acme.entities.airport.Airport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	int							rankingPosition;

	int							yearsToRetire;

	Double						onTimeLegsRatio;

	Double						delayedLegsRatio;

	Airport						mostPopularAP;

	Airport						leastPopularAP;

	int							completedLegs;

	int							pendinglegs;

	int							cancelledLegs;

	Double						avgFlightCost;

	Double						minFlightCost;

	Double						maxFlightCost;

	Double						deviationFlightCost;

}
