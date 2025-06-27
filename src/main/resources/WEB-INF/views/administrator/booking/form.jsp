<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form  readonly="${readonly}">
	<acme:input-textbox code="administrator.booking.form.label.locatorCode" path="locatorCode"/>	
	<acme:input-textbox code="administrator.booking.form.label.purchaseMoment" path="purchaseMoment"/>
	<acme:input-textbox code="administrator.booking.form.label.travelClass" path="travelClass" />
	<acme:input-money code="administrator.booking.form.label.totalPrice" path="totalPrice"/>
	<acme:input-textbox code="administrator.booking.form.label.lastNibble" path="lastNibble"/>
	<acme:input-textbox code="administrator.booking.form.label.flight" path="flight"/>
	<acme:input-textbox code="administrator.booking.form.label.customer" path="customer.identifier"/>
	<acme:input-textarea code="administrator.booking.form.label.passengers" path="passengers"/>
	
	
	<acme:button  code="administrator.booking.show.button.passengers"  action="/administrator/passenger/list?bookingId=${id}" />
</acme:form>