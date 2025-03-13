
package acme.constraints;

import java.util.Comparator;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.trackinglog.TrackingLog;
import acme.entities.trackinglog.TrackingLogRepository;
import acme.entities.trackinglog.TrackingLogStatus;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Autowired
	private TrackingLogRepository repository;


	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog value, final ConstraintValidatorContext context) {
		assert context != null;
		boolean res = false;
		if (value == null)
			super.state(context, false, "*", "javax.validation.constraints.notNull.message");
		else {
			List<TrackingLog> trackingLogs = this.repository.findAllByClaimId(value.getClaim().getId());
			{
				//Comprobar que todos los trackingLogs tienen status== pending
				boolean predicate;
				predicate = trackingLogs.isEmpty() || trackingLogs.stream().allMatch(x -> x.getStatus().equals(TrackingLogStatus.PENDING));
				super.state(context, !predicate, "status", "acme.validation.TrackingLog.statusIsFinished");
			}
			{
				//Comprobar que el aumento de porcentaje es lineal
				boolean predicate2;
				double maximum = 0.;
				if (!trackingLogs.isEmpty())
					maximum = trackingLogs.stream().max(Comparator.comparing(TrackingLog::getResolutionPercentage)).get().getResolutionPercentage();
				predicate2 = maximum == 0. || value.getResolutionPercentage() > maximum;
				super.state(context, !predicate2, "resolutionPercentage", "acme.validation.TrackingLog.resolutionPercentage-under-the-previous");
			}
			{
				//Comprobar que al ser status == FINISHED, resolutionPercentage == 100.
				boolean predicate3 = value.getResolutionPercentage() == 100. && value.getStatus() != TrackingLogStatus.PENDING || value.getResolutionPercentage() != 100. && value.getStatus() == TrackingLogStatus.PENDING;
				super.state(context, predicate3, "status", "acme.validation.TrackingLog.status-and-resolutionPercentage-don´t-match.");
			}
			{
				//El último momento de actualización debe ser posterior al que había
				boolean isAfter = false;
				TrackingLog lastDate;
				if (!trackingLogs.isEmpty()) {
					lastDate = trackingLogs.stream().max(Comparator.comparing(TrackingLog::getLastUpdate)).get();
					isAfter = value.getLastUpdate().after(lastDate.getLastUpdate());
				}
				super.state(context, !isAfter, "lastUpdate", "acme.validation.TrackingLog.lastUpdate-must-be-later-than-the-previous-one");
			}
		}
		res = !super.hasErrors(context);
		return res;
	}

}
