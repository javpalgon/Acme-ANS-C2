<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	<jstl:if test="${acme:matches(isDraftMode, 'false')}">
		<acme:input-textbox code="assistance-agent.tracking-log.form.label.lastUpdate" path="lastUpdate" readonly="${!isDraftMode}"/>
	</jstl:if>
	<acme:input-textarea code="assistance-agent.tracking-log.form.label.step" path="step" readonly="${!isDraftMode}"/>
	<acme:input-textbox code="assistance-agent.tracking-log.form.label.resolutionPercentage" path="resolutionPercentage" readonly="${!isDraftMode}"/>
	<acme:input-select code="assistance-agent.tracking-log.form.label.status" path="status" choices="${status}" readonly="${!isDraftMode}"/>
	<acme:input-textbox code="assistance-agent.tracking-log.form.label.passengerEmail" path="passengerEmail" readonly="true"/>
	<acme:input-textbox code="assistance-agent.tracking-log.form.label.description" path="description" readonly="true"/>
	<acme:input-textbox code="assistance-agent.tracking-log.form.label.resolution" path="resolution" readonly="${!isDraftMode}"/>
	<jstl:choose>
	<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish')}">
		<!-- Solo visible si está en modo borrador -->
		<jstl:if test="${acme:matches(isDraftMode, 'true')}">
			<acme:submit code="assistance-agent.tracking-log.form.button.delete" action="/assistance-agent/tracking-log/delete"/>
			<acme:submit code="assistance-agent.tracking-log.form.button.publish" action="/assistance-agent/tracking-log/publish"/>
			<acme:submit code="assistance-agent.tracking-log.form.button.update" action="/assistance-agent/tracking-log/update"/>
		</jstl:if>
	</jstl:when>

	<jstl:when test="${_command == 'create'}">
		<acme:submit code="assistance-agent.tracking-log.form.button.create" action="/assistance-agent/tracking-log/create?masterId=${masterId}"/>
	</jstl:when>
</jstl:choose>
</acme:form>
