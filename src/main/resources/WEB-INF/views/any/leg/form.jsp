<%@page language="java"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.leg.list.label.flightNumber" path="flightNumber" readonly="true"/>
	<acme:input-moment code="any.leg.list.label.departure" path="departure" readonly="true"/>	
	<acme:input-moment code="any.leg.list.label.arrival" path="arrival" readonly="true"/>	
	<acme:input-textbox code="any.leg.list.label.status" path="status" readonly="true"/>
	<acme:input-textbox code="any.leg.list.label.arrivalAirport" path="arrivalAirport" readonly="true"/>
	<acme:input-textbox code="any.leg.list.label.departureAirport" path="departureAirport" readonly="true"/>
	<acme:input-textbox code="any.leg.list.label.aircraft" path="aircraft" readonly="true"/>
	<acme:input-moment code="any.leg.list.label.duration" path="duration" readonly="true"/>				
</acme:form>