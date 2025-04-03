<%@page language="java"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-moment code="technician.maintenance-record.form.label.maintenanceTimestamp" path="maintenanceTimestamp"/>

    <acme:input-select code="technician.maintenance-record.form.label.maintenanceStatus" path="maintenanceStatus" choices="${maintenanceStatus}"/>

    <acme:input-moment code="technician.maintenance-record.form.label.nextInspectionDate" path="nextInspectionDate"/>
   
    <acme:input-money code="technician.maintenance-record.form.label.estimatedCost" path="estimatedCost"/>
    
    <acme:input-select code="technician.maintenance-record.form.label.technician" path="technician" choices="${technicians}"/>	
	
	<acme:input-select code="technician.maintenance-record.form.label.aircraft" path="aircraft" choices="${aircrafts}"/>
    
    <acme:input-textbox code="technician.maintenance-record.form.label.notes" path="notes"/>
    
    <acme:input-checkbox code="technician.maintenance-record.form.label.isDraftMode" path="isDraftMode"/>
    
    <jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish|delete')&& isDraftMode}">
			<acme:submit code="technician.maintenance-record.form.button.update" action="/technician/maintenance-record/update"/>
			<acme:submit code="technician.maintenance-record.form.button.publish" action="/technician/maintenance-record/publish"/>
			<acme:button code="technician.maintenance-record.form.button.tasks" action="/technician/involves/list?masterId=${id}"/>
			<acme:submit code="technician.maintenance-record.form.button.delete" action="/technician/maintenance-record/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="technician.maintenance-record.form.button.create" action="/technician/maintenance-record/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>