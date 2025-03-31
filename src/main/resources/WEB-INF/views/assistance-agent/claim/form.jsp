
<<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	<acme:input-textbox code="assistance-agent.claim.form.label.registeredAt" path="registeredAt"/>
	<acme:input-email code="assistance-agent.claim.form.label.passengerEmail" path="passengerEmail"/>
	<acme:input-textarea code="assistance-agent.claim.form.label.description" path="description"/>
	<acme:input-textbox code="assistance-agent.claim.form.label.type" path="type"/>	
	<acme:input-textbox code="assistance-agent.claim.form.label.accepted" path="accepted"/>	
	<acme:input-textbox code="assistance-agent.claim.form.label.isDraftMode" path="isDraftMode"/>	
	<jstl:choose>
		<jstl:when test="${acme:matches(accepted,'PENDING')}">
			<acme:input-textbox code="assistance-agent.claim.form.label.leg.flightNumber" path="leg.flightNumber"/>	
			<acme:input-textbox code="assistance-agent.claim.form.label.leg.arrival" path="leg.arrival"/>	
			<acme:input-textbox code="assistance-agent.claim.form.label.leg.departure" path="leg.departure"/>				
			<acme:input-textbox code="assistance-agent.claim.form.label.leg.status" path="leg.status"/>	
		</jstl:when>
		<jstl:when test="${_command=='show' && isDraftMode == true}">
				<acme:button code="assistance-agent.claim.form.button.trackingLogs" action="/assistance-agent/trackingLog/list?masterId=${id}"/>
		</jstl:when>
	</jstl:choose>
</acme:form>
