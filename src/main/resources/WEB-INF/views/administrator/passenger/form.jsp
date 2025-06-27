<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="administrator.passenger.form.label.fullName" path="fullName"/>	
	<acme:input-textbox code="administrator.passenger.form.label.passport" path="passport"/>	
	<acme:input-url code="administrator.passenger.form.label.email" path="email"/>
	<acme:input-moment code="administrator.passenger.form.label.birth" path="birth"/>
	<acme:input-textbox code="administrator.passenger.form.label.specialNeeds" path="specialNeeds"/>
	<input type="hidden" name="bookingId" value="${bookingId}"/>
	
</acme:form>