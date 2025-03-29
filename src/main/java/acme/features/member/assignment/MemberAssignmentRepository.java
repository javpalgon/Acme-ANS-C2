
package acme.features.member.assignment;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.assignment.Assignment;
import acme.entities.assignment.AssignmentStatus;
import acme.entities.assignment.Role;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;
import acme.realms.Member;

@Repository
public interface MemberAssignmentRepository extends AbstractRepository {

	@Query("SELECT a FROM Assignment a WHERE a.id = :id")
	Assignment findOneById(int id);

	@Query("SELECT a FROM Assignment a WHERE a.member = :member AND a.role = :role AND a.isDraftMode = false")
	List<Assignment> findByMemberAndRole(Member member, Role role);

	@Query("SELECT m FROM Member m WHERE m.id = :id")
	Member findMemberById(int id);

	@Query("SELECT a FROM Assignment a WHERE a.leg.status = :status AND a.member.id = :memberId AND a.leg.isDraftMode = false")
	Collection<Assignment> findByLegStatusAndMemberId(@Param("status") LegStatus status, @Param("memberId") int memberId);

	@Query("SELECT a FROM Assignment a WHERE a.leg.status != :status AND a.member.id = :memberId AND a.leg.isDraftMode = false")
	Collection<Assignment> findByLegStatusNotAndMemberId(@Param("status") LegStatus status, @Param("memberId") int memberId);

	@Query("SELECT DISTINCT a.leg FROM Assignment a WHERE a.member.id = :memberId")
	Collection<Leg> findLegsByMemberId(int memberId);

	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegById(int legId);

	@Query("SELECT m FROM Member m")
	List<Member> findAllMembers();

	@Query("SELECT l FROM Leg l")
	List<Leg> findAllLegs();

	@Query("SELECT a FROM Assignment a WHERE a.id = :assignmentId")
	Assignment findAssignmentById(int assignmentId);

	@Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leg l WHERE l.id = :legId AND l.departure < :currentDate")
	boolean hasLegOccurred(@Param("legId") int legId, @Param("currentDate") Date currentDate);

	@Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Assignment a WHERE a.leg.id = :legId AND a.role = :role")
	boolean legHasPilot(int legId, Role role);

	@Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Assignment a WHERE a.leg.id = :legId AND a.role = :role")
	boolean legHasCoPilot(int legId, Role role);

	@Query("SELECT COUNT(a) > 0 FROM Assignment a WHERE " + "a.member.id = :memberId AND " + "a.leg.id = :legId AND " + "a.id <> :excludeAssignmentId")
	boolean existsByMemberAndLeg(@Param("memberId") Integer memberId, @Param("legId") Integer legId, @Param("excludeAssignmentId") Integer excludeAssignmentId);

	@Query("SELECT COUNT(a) > 0 FROM Assignment a WHERE " + "a.member.id = :memberId AND " + "a.status <> :cancelledStatus AND " + "a.id <> :excludeAssignmentId AND " + "((a.leg.departure < :arrival AND a.leg.arrival > :departure))")
	boolean hasScheduleConflict(@Param("memberId") Integer memberId, @Param("departure") Date departure, @Param("arrival") Date arrival, @Param("excludeAssignmentId") Integer excludeAssignmentId, @Param("cancelledStatus") AssignmentStatus cancelledStatus);

}
