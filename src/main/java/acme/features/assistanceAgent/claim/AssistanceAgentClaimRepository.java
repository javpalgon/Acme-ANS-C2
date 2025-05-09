
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.leg.Leg;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.assistanceAgent.id = :agentId")
	Collection<Claim> findClaimsByAgentId(final int agentId);

	/*
	 * @Query("select c from Claim c where c.assistanceAgent.id = :agentId and c.accepted != :status")
	 * Collection<Claim> findClaimsByNotStatusAgentId(final int agentId, ClaimStatus status);
	 */

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(final int id);

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegById(final int legId);

	@Query("select l from Leg l where l.isDraftMode = false")
	Collection<Leg> findAllPublishedLegs();

	@Query("select l from Leg l where l.flightNumber = :flightNumber")
	Leg findLegByFlightNumber(String flightNumber);

	@Modifying
	@Transactional
	@Query("DELETE FROM TrackingLog tl WHERE tl.claim.id = :claimId")
	void deleteTrackingLogsByClaimId(int claimId);
}
