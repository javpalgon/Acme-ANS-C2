
package acme.features.member.activityLog;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activitylog.ActivityLog;
import acme.entities.assignment.Assignment;

@Repository
public interface MemberActivityLogRepository extends AbstractRepository {

	@Query("SELECT al FROM ActivityLog al WHERE al.assignment.member.id = :memberId AND al.assignment.id = :assignmentId")
	List<ActivityLog> findByMemberIdAndAssignmentId(@Param("memberId") int memberId, @Param("assignmentId") int assignmentId);

	@Query("SELECT a FROM Assignment a WHERE a.id = :id")
	Assignment findAssignmentById(int id);

	@Query("SELECT al FROM ActivityLog al WHERE al.id = :id")
	ActivityLog findActivityLogById(int id);

	@Query("SELECT al FROM ActivityLog al WHERE al.assignment.id = :masterId")
	Collection<ActivityLog> findActivityLogsByMasterId(int masterId);

	@Query("SELECT al.assignment FROM ActivityLog al WHERE al.id = :id")
	Assignment findAssignmentByActivityLogId(int id);

}
