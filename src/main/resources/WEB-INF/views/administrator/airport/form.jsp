<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="administrator.airport.form.label.name" path="name"  />
    <acme:input-textbox code="administrator.airport.form.label.IATACode" path="IATACode"  />
    <acme:input-select code="administrator.airport.form.label.operationalScope" path="operationalScope" choices="${operationalScopes}" />
    <acme:input-textbox code="administrator.airport.form.label.city" path="city" />
    <acme:input-textbox code="administrator.airport.form.label.country" path="country" />
    <acme:input-url code="administrator.airport.form.label.website" path="website"  />
    <acme:input-url code="administrator.airport.form.label.emailAddress" path="emailAddress"/>
    <acme:input-textbox code="administrator.airport.form.label.phoneNumber" path="phoneNumber"/>

	<jstl:choose>	 
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="administrator.airport.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.airport.button.create" action="/administrator/airport/create"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">			
			<acme:input-checkbox code="administrator.airport.form.label.confirmation" path="confirmation"/>
			<acme:submit code="administrator.airport.button.update" action="/administrator/airport/update"/>
		</jstl:when>		
	</jstl:choose>

</acme:form>
