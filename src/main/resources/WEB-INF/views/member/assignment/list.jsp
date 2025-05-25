<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>


<acme:list>
    <acme:list-column code="member.assignment.list.label.role" path="role" width="20%"/>
    <acme:list-column code="member.assignment.list.label.lastUpdate" path="lastUpdate" width="20%"/>
    <acme:list-column code="member.assignment.list.label.status" path="status" width="15%"/>
    <acme:list-column code="member.assignment.list.label.remarks" path="remarks" width="25%"/>
    <acme:list-column code="member.assignment.list.label.leg" path="leg.flightNumber" width="20%"/>
</acme:list>

<jstl:if test="${memberAvailable}">
    <acme:button code="member.assignment.list.button.create" action="/member/assignment/create"/>
</jstl:if>
