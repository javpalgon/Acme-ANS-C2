<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="administrator.aircraft.label.model" path="model"/>
	<acme:input-textbox code="administrator.aircraft.label.regitrationNumber" path="regitrationNumber"/>	
	<acme:input-integer code="administrator.aircraft.label.capacity" path="capacity"/>
	<acme:input-integer code="administrator.aircraft.label.cargoWeight" path="cargoWeight"/>
	<acme:input-select code="administrator.aircraft.label.aircraftStatus" path="aircraftStatus" choices="${aircraftStatus}"/>
	<acme:input-textarea code="administrator.aircraft.label.details" path="details"/>
	<acme:input-select code="administrator.aircraft.label.airline" path="airline" choices="${airlines}"/>
	<acme:input-textbox code="administrator.aircraft.label.name" path="name" readonly="true"/>
	<acme:input-url code="administrator.aircraft.label.website" path="website" readonly="true"/> 
	<jstl:choose>
		<jstl:when test="${acme:matches(_command,'show|update')}">			
			<acme:input-checkbox code="administrator.aircraft.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.aircraft.button.update" action="/administrator/aircraft/update"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="administrator.airline.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.aircraft.button.create" action="/administrator/aircraft/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>