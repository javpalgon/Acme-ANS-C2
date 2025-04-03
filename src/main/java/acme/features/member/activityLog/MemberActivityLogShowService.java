
package acme.features.member.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.realms.Member;

@GuiService
public class MemberActivityLogShowService extends AbstractGuiService<Member, ActivityLog> {

	@Autowired
	private MemberActivityLogRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registeredAt", "incidentType", "description", "severityLevel", "isDraftMode");
		dataset.put("masterId", activityLog.getAssignment().getId());
		dataset.put("masterIsDraftMode", activityLog.getAssignment().getIsDraftMode());

		super.getResponse().addData(dataset);
	}

}
