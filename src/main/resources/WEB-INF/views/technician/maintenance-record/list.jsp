<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.maintenance-record.list.label.maintenanceTimestamp" path="maintenanceTimestamp"  width="20%"/>
	<acme:list-column code="technician.maintenance-record.list.label.maintenanceStatus" path="maintenanceStatus" width="20%" />
	<acme:list-column code="technician.maintenance-record.list.label.nextInspectionDate" path="nextInspectionDate" width="20%" />
	<acme:list-column code="technician.maintenance-record.list.label.estimatedCost" path="estimatedCost" width="20%" />
	<acme:list-column code="technician.maintenance-record.list.label.notes" path="notes" width="20%" />
	<acme:list-column code="technician.maintenance-record.list.label.isDraftMode" path="isDraftMode" width="20%" />
	
	
</acme:list>

<jstl:if test="${_command == 'list'}">
	<acme:button code="technician.maintenance-record.list.button.create" action="/technician/maintenance-record/create"/>
</jstl:if>	