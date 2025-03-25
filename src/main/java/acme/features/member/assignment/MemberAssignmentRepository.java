
package acme.features.member.assignment;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.assignment.Assignment;
import acme.entities.assignment.Role;
import acme.entities.leg.LegStatus;
import acme.realms.Member;

@Repository
public interface MemberAssignmentRepository extends AbstractRepository {

	@Query("SELECT a FROM Assignment a WHERE a.id = :id")
	Assignment findOneById(int id);

	@Query("SELECT a FROM Assignment a WHERE a.member = :member AND a.role = :role")
	List<Assignment> findByMemberAndRole(Member member, Role role);

	@Query("SELECT m FROM Member m WHERE m.id = :id")
	Member findMemberById(int id);

	@Query("SELECT a FROM Assignment a")
	Collection<Assignment> findAllAsignments();

	@Query("SELECT a FROM Assignment a WHERE a.leg.status = :status AND a.member.id = :memberId")
	Collection<Assignment> findByLegStatusAndMemberId(@Param("status") LegStatus status, @Param("memberId") int memberId);

	@Query("SELECT a FROM Assignment a WHERE a.leg.status != :status AND a.member.id = :memberId")
	Collection<Assignment> findByLegStatusNotAndMemberId(@Param("status") LegStatus status, @Param("memberId") int memberId);

}
