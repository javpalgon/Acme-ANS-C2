yo <%--
- list.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="agents.trackingLog.application.list.label.lastUpdate" path="lastUpdate" width="20%"/>
	<acme:list-column code="agents.trackingLog.application.list.label.step" path="step" width="20%"/>
	<acme:list-column code="agents.trackingLog.application.list.label.resolutionPercentage" path="resolutionPercentage" width="20%"/>
	<acme:list-column code="agents.trackingLog.application.list.label.status" path="status" width="10%"/>
	<acme:list-payload path="payload"/>
</acme:list>
<jstl:if test="${!dontShowCreate}">
	<acme:button code="assistance-agent.tracking-log.list.button.create" action="/assistance-agent/tracking-log/create?masterId=${masterId}"/>
</jstl:if>

