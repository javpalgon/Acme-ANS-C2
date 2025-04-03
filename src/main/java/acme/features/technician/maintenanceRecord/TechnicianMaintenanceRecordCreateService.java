
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
		//		assert object != null;
		//		int technicianId;
		//		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		//		final Technician technician = this.rp.findTechnicianById(technicianId);
		//		object.setTechnician(technician);
		Integer aircraftId;
		Aircraft aircraft;

		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.rp.findAircraftById(aircraftId);

		Integer technicianId;
		Technician technician;

		technicianId = super.getRequest().getData("technician", int.class);
		technician = this.rp.findTechnicianById(technicianId);
		super.bindObject(object, "maintenanceTimestamp", "maintenanceStatus", "nextInspectionDate", "estimatedCost", "notes", "isDraftMode");
		object.setAircraft(aircraft);
		object.setTechnician(technician);
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
		//		Dataset dataset;
		SelectChoices maintenanceRecordStatus;
		SelectChoices aircraftChoices;
		SelectChoices technicianChoices;

		Collection<Technician> technicians;
		Dataset dataset;

		technicians = this.rp.findAllTechnicians();

		technicianChoices = SelectChoices.from(technicians, "licenseNumber", object.getTechnician());

		Collection<Aircraft> aircrafts;

		aircrafts = this.rp.findAllAircrafts();

		aircraftChoices = SelectChoices.from(aircrafts, "model", object.getAircraft());

		maintenanceRecordStatus = new SelectChoices();
		maintenanceRecordStatus.add("0", "-------", object.getMaintenanceStatus() == null);
		maintenanceRecordStatus.add("PENDING", "PENDING", object.getMaintenanceStatus() == MaintenanceStatus.PENDING);
		maintenanceRecordStatus.add("IN_PROGRESS", "IN_PROGRESS", object.getMaintenanceStatus() == MaintenanceStatus.IN_PROGRESS);
		dataset = super.unbindObject(object, "maintenanceTimestamp", "maintenanceStatus", "nextInspectionDate", "estimatedCost", "notes", "isDraftMode");
		dataset.put("maintenanceStatus", maintenanceRecordStatus);
		dataset.put("technicians", technicianChoices);
		dataset.put("technician", technicianChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		super.getResponse().addData(dataset);
	}
}
