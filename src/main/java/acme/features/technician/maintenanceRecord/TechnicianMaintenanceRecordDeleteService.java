
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.MaintenanceStatus;
import acme.entities.task.Involves;
import acme.entities.task.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordDeleteService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	protected TechnicianMaintenanceRecordRepository rp;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		MaintenanceRecord object;
		int maintenanceId;

		maintenanceId = super.getRequest().getData("id", int.class);
		object = this.rp.findMaintenanceRecordById(maintenanceId);
		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final MaintenanceRecord object) {
		assert object != null;
		int technicianId;
		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		final Technician technician = this.rp.findTechnicianById(technicianId);
		object.setTechnician(technician);
		super.bindObject(object, "maintenanceTimestamp", "maintenanceStatus", "nextInspectionDate", "estimatedCost", "notes", "isDraftMode");
	}

	@Override
	public void perform(final MaintenanceRecord object) {
		assert object != null;

		Collection<Involves> allInvolves = this.rp.findAllInvolvesByMaintenanceRecord(object.getId());
		Collection<Task> allTask = this.rp.findAllTaskByMaintenanceRecord(object.getId());

		this.rp.deleteAll(allTask);
		this.rp.deleteAll(allInvolves);
		this.rp.delete(object);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		SelectChoices aircraftChoices;
		SelectChoices technicianChoices;
		SelectChoices maintenanceRecordStatus;

		Collection<Technician> technicians;
		Dataset dataset;

		technicians = this.rp.findAllTechnicians();

		technicianChoices = SelectChoices.from(technicians, "licenseNumber", maintenanceRecord.getTechnician());

		Collection<Aircraft> aircrafts;

		aircrafts = this.rp.findAllAircrafts();

		aircraftChoices = SelectChoices.from(aircrafts, "model", maintenanceRecord.getAircraft());

		maintenanceRecordStatus = SelectChoices.from(MaintenanceStatus.class, maintenanceRecord.getMaintenanceStatus());

		dataset = super.unbindObject(maintenanceRecord, "maintenanceTimestamp", "maintenanceStatus", "nextInspectionDate", "estimatedCost", "notes", "isDraftMode");
		dataset.put("maintenanceStatus", maintenanceRecordStatus);
		dataset.put("technicians", technicianChoices);
		dataset.put("technician", technicianChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

	@Override
	public void validate(final MaintenanceRecord object) {
		assert object != null;
		if (!object.getIsDraftMode())
			super.state(object.getIsDraftMode(), "*", "technician.maintenance-record.form.error.notDraft", "isDraftMode");
	}

}
