<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>


<acme:list>
    <acme:list-column code="administrator.passenger.list.label.fullName" path="fullName"/>
    <acme:list-column code="administrator.passenger.list.label.passport" path="passport"/>
    <acme:list-column code="administrator.passenger.list.label.email" path="email"/>
    <acme:list-column code="administrator.passenger.list.label.birth" path="birth"/>
    <acme:list-column code="administrator.passenger.list.label.specialNeeds" path="specialNeeds"/>
</acme:list>
