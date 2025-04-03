
package acme.features.technician.maintenanceRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.realms.Technician;

@GuiController
public class TechnicianMaintenanceRecordController extends AbstractGuiController<Technician, MaintenanceRecord> {

	@Autowired
	protected TechnicianMaintenanceRecordCreateService	createService;

	@Autowired
	protected TechnicianMaintenanceRecordListService	listService;

	@Autowired
	protected TechnicianMaintenanceRecordShowService	showService;

	@Autowired
	protected TechnicianMaintenanceRecordDeleteService	deleteService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("delete", this.deleteService);

	}

}
