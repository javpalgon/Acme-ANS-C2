
package acme.features.member.assignment;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import acme.client.repositories.AbstractRepository;
import acme.entities.assignment.Assignment;
import acme.entities.assignment.AssignmentStatus;
import acme.entities.assignment.Role;
import acme.entities.flightcrewmember.AvailabilityStatus;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;
import acme.realms.Member;

@Repository
public interface MemberAssignmentRepository extends AbstractRepository {

	@Query("SELECT a FROM Assignment a WHERE a.id = :id")
	Assignment findOneById(int id);

	//	@Query("SELECT m FROM Member m")
	//	List<Member> findAllMembers();

	//	@Query("SELECT a FROM Assignment a")
	//	List<Assignment> findAllAssignments();

	@Query("SELECT l FROM Leg l")
	List<Leg> findAllLegs();

	@Query("SELECT a FROM Assignment a WHERE a.member = :member AND a.role = :role AND a.draftMode = false")
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

	@Query("SELECT m FROM Member m WHERE m.availabilityStatus = :status")
	List<Member> findAvailableMembers(@Param("status") AvailabilityStatus status);

	@Query("SELECT l FROM Leg l " + "WHERE l.departure > :currentDate " + "AND l.isDraftMode = false " + "AND l.flight.isDraftMode = false " + "AND l.aircraft.airline.id = :airlineId")
	List<Leg> findAllPFL(@Param("currentDate") Date currentDate, @Param("airlineId") int airlineId);

	@Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Leg l WHERE l.id = :legId AND l.departure < :currentDate")
	boolean hasLegOccurred(@Param("legId") int legId, @Param("currentDate") Date currentDate);

	@Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Assignment a WHERE " + "a.leg.id = :legId AND a.role = :role AND a.status != :cancelledStatus")
	boolean legHasPilot(@Param("legId") int legId, @Param("role") Role role, @Param("cancelledStatus") AssignmentStatus cancelledStatus);

	@Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Assignment a WHERE " + "a.leg.id = :legId AND a.role = :role AND a.status != :cancelledStatus")
	boolean legHasCoPilot(@Param("legId") int legId, @Param("role") Role role, @Param("cancelledStatus") AssignmentStatus cancelledStatus);

	@Query("SELECT COUNT(a) > 0 FROM Assignment a WHERE " + "a.member.id = :memberId AND a.leg.id = :legId AND " + "a.id <> :excludeAssignmentId AND a.status != :cancelledStatus")
	boolean existsByMemberAndLeg(@Param("memberId") Integer memberId, @Param("legId") Integer legId, @Param("excludeAssignmentId") Integer excludeAssignmentId, @Param("cancelledStatus") AssignmentStatus cancelledStatus);

	@Query("SELECT COUNT(a) > 0 FROM Assignment a WHERE " + "a.member.id = :memberId AND a.status <> :cancelledStatus AND " + "a.id <> :excludeAssignmentId AND " + "((a.leg.departure < :arrival AND a.leg.arrival > :departure))")
	boolean hasScheduleConflict(@Param("memberId") Integer memberId, @Param("departure") Date departure, @Param("arrival") Date arrival, @Param("excludeAssignmentId") Integer excludeAssignmentId, @Param("cancelledStatus") AssignmentStatus cancelledStatus);

	//	@Query("SELECT COUNT(a) > 0 FROM Assignment a WHERE " + "a.leg.id = :legId AND a.role = 'PILOT' AND " + "a.id != :excludeId AND a.status != :cancelledStatus")
	//	boolean legHasOtherPilot(@Param("legId") Integer legId, @Param("excludeId") Integer excludeId, @Param("cancelledStatus") AssignmentStatus cancelledStatus);
	//
	//	@Query("SELECT COUNT(a) > 0 FROM Assignment a WHERE " + "a.leg.id = :legId AND a.role = 'CO_PILOT' AND " + "a.id != :excludeId AND a.status != :cancelledStatus")
	//	boolean legHasOtherCoPilot(@Param("legId") Integer legId, @Param("excludeId") Integer excludeId, @Param("cancelledStatus") AssignmentStatus cancelledStatus);

	@Modifying
	@Transactional
	@Query("DELETE FROM ActivityLog al WHERE al.assignment.id = :assignmentId AND al.draftMode = true")
	void deleteActivityLogsByAssignmentId(int assignmentId);

	@Query("select m from Member m where m.employeeCode = :employeeCode")
	Member findMemberByEmployeeCode(String employeeCode);

}
