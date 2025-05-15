<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.aircraft.label.model" path="model"/>
	<acme:list-column code="administrator.aircraft.label.regitrationNumber" path="regitrationNumber"/>
	<acme:list-column code="administrator.aircraft.label.capacity" path="capacity"/>
	<acme:list-column code="administrator.aircraft.label.aircraftStatus" path="aircraftStatus"/>
	
	<acme:list-payload path="payload"/>
</acme:list>	

<jstl:if test="${_command == 'list'}">
	<acme:button code="administrator.airline.button.create" action="/administrator/aircraft/create"/>
</jstl:if>