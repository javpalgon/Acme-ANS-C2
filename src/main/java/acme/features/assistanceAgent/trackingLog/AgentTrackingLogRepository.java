
package acme.features.assistanceAgent.trackingLog;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.trackinglog.TrackingLog;

@Repository
public interface AgentTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.claim.assistanceAgent.id = :assistanceAgentId and t.claim.id = :claimId")
	List<TrackingLog> findAllByAssistanceAgentAndClaimId(int assistanceAgentId, int claimId);
}
