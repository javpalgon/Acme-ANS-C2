
package acme.features.technician.maintenanceRecord;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordListService extends AbstractGuiService<Technician, MaintenanceRecord> {

	private static final Logger						logger	= LoggerFactory.getLogger(TechnicianMaintenanceRecordListService.class);

	@Autowired
	private TechnicianMaintenanceRecordRepository	rp;


	@Override
	public void authorise() {
		// TODO Auto-generated method stub
		//	boolean status = super.getRequest().getPrincipal().isAuthenticated();
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		Collection<MaintenanceRecord> records = new ArrayList<>();
		int technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		records = this.rp.findMaintenaceRecordsByTechnician(technicianId);
		super.getBuffer().addData(records);
	}

	@Override
	public void unbind(final MaintenanceRecord object) {
		// TODO Auto-generated method stub
		assert object != null;
		Dataset dataset;
		dataset = super.unbindObject(object, "maintenanceTimestamp", "maintenanceStatus", "nextInspectionDate", "estimatedCost", "notes", "isDraftMode");
		super.getResponse().addData(dataset);
	}

}
