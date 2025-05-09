
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.trackinglog.TrackingLog;

@Repository
public interface AgentTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.claim.assistanceAgent.id = :assistanceAgentId and t.claim.id = :claimId")
	List<TrackingLog> findAllByAssistanceAgentAndClaimId(int assistanceAgentId, int claimId);

	@Query("select t.claim from TrackingLog t where t.id = :trackingLogId")
	Claim findClaimByTrackingLogId(int trackingLogId);

	@Query("select t from TrackingLog t where t.id = :trackingLogId")
	TrackingLog findTrackingLogById(int trackingLogId);

	@Query("select c from Claim c where c.id = :claimId")
	Claim findClaimById(int claimId);

	@Query("select t from TrackingLog t where t.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);
}
