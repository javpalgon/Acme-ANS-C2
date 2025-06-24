<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.flight.form.label.tag" path="tag" readonly="true"/>	
	<acme:input-money code="any.flight.form.label.cost" path="cost" readonly="true"/>	
	<acme:input-textarea code="any.flight.form.label.description" path="description" readonly="true"/>	
	<acme:input-checkbox code="any.flight.form.label.requiresSelfTransfer" path="requiresSelfTransfer" readonly="true"/>
	<acme:input-moment code="any.flight.form.label.departure" path="departure" readonly="true"/>
	<acme:input-moment code="any.flight.form.label.arrival" path="arrival" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.originCity" path="originCity" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.destinationCity" path="destinationCity" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.layovers" path="layovers" readonly="true"/>
	<jstl:choose>	 
		<jstl:when test="${_command == 'show' && isDraftMode == false}">
			<acme:button code="any.flight.leg" action="/any/leg/list?masterId=${id}"/>
		</jstl:when>		
	</jstl:choose>
	
</acme:form>