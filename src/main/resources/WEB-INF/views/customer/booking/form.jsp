<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="customer.booking.form.label.locatorCode" path="locatorCode"/>	
	<acme:input-textbox code="customer.booking.form.label.purchaseMoment" path="purchaseMoment"/>	
	<acme:input-textbox code="customer.booking.form.label.travelClass" path="travelClass"/>	
	<acme:input-money code="customer.booking.form.label.totalPrice" path="totalPrice"/>
	<acme:input-textbox code="customer.booking.form.label.lastNibble" path="lastNibble"/>
	<acme:input-checkbox code="customer.booking.form.label.isDraftMode" path="isDraftMode"/>	
	<jstl:if test="${hasPassengers}">
		<acme:input-textarea code="customer.booking.form.label.passengers" path="passengers"/>	
	</jstl:if>

	<jstl:if test="${!hasPassengers}">
	        <p><em>No passengers for this booking.</em></p>
	</jstl:if>	
  
</acme:form>