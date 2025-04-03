<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
    <acme:list-column code="member.activityLog.list.label.incidentType" path="incidentType" width="25%"/>
    <acme:list-column code="member.activityLog.list.label.description" path="description" width="40%"/>
    <acme:list-column code="member.activityLog.list.label.severityLevel" path="severityLevel" width="15%"/>
    <acme:list-column code="member.activityLog.list.label.registeredAt" path="registeredAt" width="20%"/>

	<acme:list-payload path="payload"/>
</acme:list>	
 
 <jstl:if test="${showCreate}">
 	<acme:button code="member.activity-log.list.button.create" action="/member/activity-log/create?masterId=${masterId}"/>
 </jstl:if>
