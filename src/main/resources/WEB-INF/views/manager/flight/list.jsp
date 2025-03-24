
<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.flight.list.label.tag" path="tag"  width="40%"/>
	<acme:list-column code="manager.flight.list.label.cost" path="cost" width="40%" />
	<acme:list-column code="manager.flight.list.label.description" path="description" width="20%" />
	<acme:list-column code="manager.flight.list.label.requiresSelfTransfer" path="requiresSelfTransfer" width="20%" />
	
</acme:list>
<acme:button code="manager.flight.create" action="/manager/flight/create"/>