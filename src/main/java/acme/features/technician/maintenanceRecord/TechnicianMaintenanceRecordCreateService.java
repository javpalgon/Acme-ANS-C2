
package acme.features.technician.maintenanceRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	protected TechnicianMaintenanceRecordRepository rp;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		MaintenanceRecord object;
		object = new MaintenanceRecord();
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		final Technician technician = this.rp.findTechnicianById(technicianId);
		object.setTechnician(technician);
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
		this.rp.save(object);
	}

	@Override
	public void validate(final MaintenanceRecord mr) {
		;
	}

	@Override
	public void unbind(final MaintenanceRecord object) {
		assert object != null;
		Dataset dataset;
		dataset = super.unbindObject(object, "maintenanceTimestamp", "maintenanceStatus", "nextInspectionDate", "estimatedCost", "notes", "isDraftMode");
		super.getResponse().addData(dataset);
	}
}
