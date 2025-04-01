<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="customer.passenger.form.label.fullName" path="fullName"/>	
	<acme:input-textbox code="customer.passenger.form.label.passport" path="passport"/>	
	<acme:input-textbox code="customer.passenger.form.label.email" path="email"/>
	<acme:input-textbox code="customer.passenger.form.label.birth" path="birth"/>
	<acme:input-textbox code="customer.passenger.form.label.specialNeeds" path="specialNeeds"/>
	<input type="hidden" name="bookingId" value="${bookingId}"/>

	<jstl:choose>
	    <jstl:when test="${(_command == 'update' ||  _command == 'show') && isDraftMode}">
	        <acme:submit code="customer.passenger.form.button.save" action="/customer/passenger/update"/>    	
	    </jstl:when>
	    <jstl:when test="${_command == 'create'}">
            <acme:submit code="customer.passenger.form.button.create" action="/customer/passenger/create"/>
        </jstl:when>
	</jstl:choose>
</acme:form>

