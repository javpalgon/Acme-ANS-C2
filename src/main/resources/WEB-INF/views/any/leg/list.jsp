<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="any.leg.list.label.flightNumber" path="flightNumber"  width="10%"/>
	<acme:list-column code="any.leg.list.label.departure" path="departure" width="10%" />
	<acme:list-column code="any.leg.list.label.arrival" path="arrival" width="10%" />
	<acme:list-column code="any.leg.list.label.status" path="status" width="10%" />	
	<acme:list-column code="any.leg.list.label.departureAirport" path="departureAirport" width="10%" />	
	<acme:list-column code="any.leg.list.label.arrivalAirport" path="arrivalAirport" width="10%" />	
	<acme:list-column code="any.leg.list.label.aircraft" path="aircraft" width="10%" />			
</acme:list>