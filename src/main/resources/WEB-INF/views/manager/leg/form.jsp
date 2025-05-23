<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>
<acme:form>
	<acme:input-textbox code="manager.leg.list.label.flightNumber" path="flightNumber" placeholder ="${IATACode}" />
	<acme:input-moment code="manager.leg.list.label.departure" path="departure"/>	
	<acme:input-moment code="manager.leg.list.label.arrival" path="arrival"/>	
	<acme:input-select code="manager.leg.list.label.status" path="status" choices="${status}"/>
	<acme:input-select code="manager.leg.list.label.arrivalAirport" path="arrivalAirport" choices="${arrivalAirports}"/>
	<acme:input-select code="manager.leg.list.label.departureAirport" path="departureAirport" choices="${departureAirports}"/>
	<acme:input-select code="manager.leg.list.label.aircraft" path="aircraft" choices="${aircrafts}"/>	
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && isDraftMode == true && isDraftFlight == true}">
			<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
			<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
			<acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.leg.create.button" action="/manager/leg/create?masterId=${masterId}"/>
		</jstl:when>		
	</jstl:choose>		
</acme:form>