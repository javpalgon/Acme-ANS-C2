
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("select m from MaintenanceRecord m where m.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select m from MaintenanceRecord m where m.technician.id = :id")
	Collection<MaintenanceRecord> findMaintenaceRecordsByTechnician(int id);

	@Query("select t from Technician t where t.id = :id")
	Technician findTechnicianById(int id);

	@Query("select t from Technician t")
	Collection<Technician> findAllTechnicians();

	@Query("select a from Aircraft a")
	Collection<Aircraft> findAllAircrafts();

}
