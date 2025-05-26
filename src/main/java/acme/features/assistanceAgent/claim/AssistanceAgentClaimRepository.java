
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.entities.claim.Claim;
import acme.entities.leg.Leg;
import acme.realms.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.assistanceAgent.id = :agentId")
	Collection<Claim> findClaimsByAgentId(final int agentId);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(final int id);

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegById(final int legId);

	@Query("select l from Leg l where l.arrival < :date and l.isDraftMode = false and l.flight.isDraftMode = false")
	Collection<Leg> findAllPublishedPastLegs(final Date date);

	@Query("select l from Leg l where l.isDraftMode = false")
	Collection<Leg> findAllPublishedLegs();

	@Query("select l from Leg l where l.flightNumber = :flightNumber")
	Leg findLegByFlightNumber(String flightNumber);

	@Query("select l from Leg l where l.arrival < :date and l.isDraftMode = false and l.flight.isDraftMode = false and l.aircraft.airline= :agentAirline")
	Collection<Leg> findPastPublishedLegsByAirline(final Date date, final Airline agentAirline);

	@Query("select a from AssistanceAgent a where a.id = :agentId")
	AssistanceAgent findAgentById(final int agentId);

	@Modifying
	@Transactional
	@Query("DELETE FROM TrackingLog tl WHERE tl.claim.id = :claimId")
	void deleteTrackingLogsByClaimId(int claimId);
}
