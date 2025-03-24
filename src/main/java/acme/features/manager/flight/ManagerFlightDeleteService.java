
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.assignment.Assignment;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;
import acme.entities.claim.Claim;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.entities.trackinglog.TrackingLog;
import acme.realms.Manager;

@GuiService
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	protected ManagerFlightRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Flight project;
		int id;

		id = super.getRequest().getData("id", int.class);
		project = this.repository.findFlightById(id);

		super.getBuffer().addData(project);
	}

	@Override
	public void bind(final Flight object) {
		assert object != null;
		int managerId;
		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		final Manager manager = this.repository.findOneManagerById(managerId);
		object.setManager(manager);
		super.bindObject(object, "tag", "cost", "description", "requiresSelfTransfer", "isDraftMode");
	}

	@Override
	public void validate(final Flight object) {
		assert object != null;
		if (!object.getIsDraftMode())
			super.state(object.getIsDraftMode(), "*", "manager.flight.form.error.notDraft", "isDraftMode");
	}
	@Override
	public void perform(final Flight object) {
		assert object != null;

		Collection<Leg> allLegs = this.repository.findLegsByFlightId(object.getId());
		Collection<Booking> allbookings = this.repository.findBookingsByFlightId(object.getId());

		for (Booking booking : allbookings) {
			Collection<BookingRecord> brecords = this.repository.findBookingRecordsByBookingId(booking.getId());
			this.repository.deleteAll(brecords);
		}
		this.repository.deleteAll(allbookings);

		for (Leg leg : allLegs) {
			Collection<Assignment> assignments = this.repository.findAssignmentsByLegId(leg.getId());
			Collection<Claim> claims = this.repository.findClaimsByLegId(leg.getId());

			for (Assignment assignment : assignments) {
				Collection<ActivityLog> activityLogs = this.repository.findActivityLogsByAssigId(assignment.getId());
				this.repository.deleteAll(activityLogs);
			}
			this.repository.deleteAll(assignments);

			for (Claim claim : claims) {
				Collection<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimId(claim.getId());
				this.repository.deleteAll(trackingLogs);
			}

			this.repository.deleteAll(claims);

			this.repository.delete(leg);
		}
		this.repository.delete(object);
	}

	@Override
	public void unbind(final Flight object) {
		assert object != null;
		Dataset dataset;
		dataset = super.unbindObject(object, "tag", "cost", "description", "requiresSelfTransfer", "isDraftMode");
		super.getResponse().addData(dataset);
	}

}
