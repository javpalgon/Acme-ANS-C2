
package acme.features.member.activityLog;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activitylog.ActivityLog;

@Repository
public interface MemberActivityLogRepository extends AbstractRepository {

	@Query("SELECT al FROM ActivityLog al WHERE al.assignment.member.id = :memberId AND al.assignment.id = :assignmentId")
	List<ActivityLog> findByMemberIdAndAssignmentId(@Param("memberId") int memberId, @Param("assignmentId") int assignmentId);

}
