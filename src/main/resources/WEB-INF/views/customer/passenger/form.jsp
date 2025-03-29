<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="customer.passenger.list.label.fullName" path="fullName"/>	
	<acme:input-textbox code="customer.passenger.list.label.passport" path="passport"/>	
	<acme:input-textbox code="customer.passenger.list.label.email" path="email"/>
	<acme:input-textbox code="customer.passenger.list.label.birth" path="birth"/>
	<acme:input-textbox code="customer.passenger.list.label.specialNeeds" path="specialNeeds"/>

</acme:form>