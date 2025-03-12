
package acme.forms.member;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.entities.assignment.Assignment;
import acme.entities.assignment.AssignmentStatus;
import acme.realms.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long						serialVersionUID	= 1L;

	private List<String>							lastDestinations;
	private String									severityRanges;
	private List<Member>							assignedCrew;
	private Map<AssignmentStatus, List<Assignment>>	assignmentsByStatus;
	private Double									avgAssignments;
	private Integer									maxAssignments;
	private Integer									minAssignments;
	private Double									stdDevAssignments;

}
