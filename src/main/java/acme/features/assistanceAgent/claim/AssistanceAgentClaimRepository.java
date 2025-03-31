
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimStatus;
import acme.entities.leg.Leg;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.assistanceAgent.id = :agentId and c.accepted = :status")
	Collection<Claim> findClaimsByStatusAndAgentId(final int agentId, ClaimStatus status);

	@Query("select c from Claim c where c.assistanceAgent.id = :agentId and c.accepted != :status")
	Collection<Claim> findClaimsByNotStatusAgentId(final int agentId, ClaimStatus status);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(final int id);

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegById(final int legId);
}
