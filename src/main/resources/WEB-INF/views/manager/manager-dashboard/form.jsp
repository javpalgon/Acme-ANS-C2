<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="manager.dashboard.form.label.managerRanking" path="managerRanking"/>
	<acme:input-textbox code="manager.dashboard.form.label.yearsToRetire" path="yearsToRetire"/>
	<acme:input-double code="manager.dashboard.form.label.delayedRatio" path="delayedRatio"/>
	<acme:input-textbox code="manager.dashboard.form.label.mostPopularAirport" path="mostPopularAirport"/>
	<acme:input-textbox code="manager.dashboard.form.label.lessPopularAirport" path="lessPopularAirport"/>
	<acme:input-textbox code="manager.dashboard.form.label.numberOfLegsPerStatusSummary" path="numberOfLegsPerStatusSummary"/>
	
	<fieldset>
	<legend><acme:print code="manager.dashboard.form.label.priceStatistics"/></legend>
	<jstl:forEach var="entry" items="${priceStatistics}">
		<div>
			<strong>${entry.key}</strong><br/>
			<acme:print code="manager.dashboard.form.label.averageFlightCost"/>: ${entry.value.average()}<br/>
			<acme:print code="manager.dashboard.form.label.minimumFlightCost"/>: ${entry.value.min()}<br/>
			<acme:print code="manager.dashboard.form.label.maximumFlightCost"/>: ${entry.value.max()}<br/>
			<acme:print code="manager.dashboard.form.label.deviationFlightCost"/>: ${entry.value.stddev()}
		</div>
		<br/>
	</jstl:forEach>
</fieldset>
	
	
	
	

</acme:form>