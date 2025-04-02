<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	<jstl:if test="${acme:matches(isDraftMode, 'false')}">
		<acme:input-textbox code="assistance-agent.claim.form.label.registeredAt" path="registeredAt" readonly="true"/>
	</jstl:if>
	<acme:input-email code="assistance-agent.claim.form.label.passengerEmail" path="passengerEmail"/>
	<acme:input-textarea code="assistance-agent.claim.form.label.description" path="description"/>
	<acme:input-select code="assistance-agent.claim.form.label.type" path="type" choices="${type}"/>
	<acme:input-textbox code="assistance-agent.claim.form.label.accepted" path="accepted" readonly="true"/>
	<acme:input-select code="assistance-agent.claim.form.label.leg.flightNumber" path="leg" choices="${leg}"/>
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish')}">
			<jstl:if test="${acme:matches(accepted,'PENDING') && acme:matches(_command, 'show')}">
				<acme:input-textbox code="assistance-agent.claim.form.label.leg.arrival" path="arrival" readonly="true"/>	
				<acme:input-textbox code="assistance-agent.claim.form.label.leg.departure" path="departure" readonly="true"/>				
				<acme:input-textbox code="assistance-agent.claim.form.label.leg.status" path="status" readonly="true"/>	
			</jstl:if>
			<jstl:if test="${acme:matches(isDraftMode, 'true')}">
				<acme:submit code="assistance-agent.claim.form.button.update" action="/assistance-agent/claim/update"/>
				<acme:submit code="assistance-agent.claim.form.button.delete" action="/assistance-agent/claim/delete"/>
				<acme:submit code="assistance-agent.claim.form.button.publish" action="/assistance-agent/claim/publish"/>
			</jstl:if>
		</jstl:when>
		<jstl:when test="${acme:matches(_command, 'create')}">
			<acme:submit code="assistance-agent.claim.form.button.create" action="/assistance-agent/claim/create"/>
		</jstl:when>			
	</jstl:choose>
	<jstl:if test="${acme:matches(_command, 'show') }">
		<acme:button code="assistance-agent.claim.trackingLogs" action="/assistance-agent/claim/list?masterId=${id}"/>	
	</jstl:if>
</acme:form>
