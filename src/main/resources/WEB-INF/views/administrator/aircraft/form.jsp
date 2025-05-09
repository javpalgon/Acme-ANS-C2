<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="administrator.aircraft.label.model" path="model"/>
	<acme:input-textbox code="administrator.aircraft.label.regitrationNumber" path="regitrationNumber"/>	
	<acme:input-url code="administrator.aircraft.label.capacity" path="capacity"/>
	<acme:input-textbox code="administrator.aircraft.label.cargoWeight" path="cargoWeight"/>
	<acme:input-select code="administrator.aircraft.label.aircraftStatus" path="aircraftStatus" choices="${aircraftStatus}"/>
	<acme:input-email code="administrator.aircraft.label.details" path="details"/>
	<acme:input-textbox code="administrator.aircraft.label.name" path="name"/>
	<acme:input-textbox code="administrator.aircraft.label.IATACode" path="IATACode"/>
	<acme:input-url code="administrator.aircraft.label.website" path="website"/>
	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">			
			<acme:input-checkbox code="administrator.airline.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.airline.button.update" action="/administrator/airline/update"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="administrator.airline.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.airline.button.create" action="/administrator/airline/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>