
package acme.entities.trackinglog;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TrackingLogRepository extends AbstractRepository {

	@Query("select * from tracking_log t where t.claim.id = :claimId")
	List<TrackingLog> findAllByClaimId(int claimId);
}
