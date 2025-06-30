<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<!-- Total number of airports grouped by their operational scope -->
	<fieldset>
		<legend><acme:print code="administrator.dashboard.form.label.totalAirportsByScope"/></legend>
		<jstl:choose>
			<jstl:when test="${not empty totalAirportsByScope}">
				<jstl:forEach var="entry" items="${totalAirportsByScope}">
					<div><strong>${entry.key}</strong>: <acme:print value="${entry.value}"/></div>
				</jstl:forEach>
			</jstl:when>
			<jstl:otherwise>
				<p><acme:print code="administrator.dashboard.form.label.no-data"/></p>
			</jstl:otherwise>
		</jstl:choose>
	</fieldset>

	<!-- Total number of airlines grouped by their type -->
	<fieldset>
		<legend><acme:print code="administrator.dashboard.form.label.totalAirlinesByType"/></legend>
		<jstl:choose>
			<jstl:when test="${not empty totalAirlinesByType}">
				<jstl:forEach var="entry" items="${totalAirlinesByType}">
					<div><strong>${entry.key}</strong>: <acme:print value="${entry.value}"/></div>
				</jstl:forEach>
			</jstl:when>
			<jstl:otherwise>
				<p><acme:print code="administrator.dashboard.form.label.no-data"/></p>
			</jstl:otherwise>
		</jstl:choose>
	</fieldset>

	<!-- Ratio of airlines with both phone and email -->
	<fieldset>
		<legend><acme:print code="administrator.dashboard.form.label.ratioAirlinesWithEmailAndPhone"/></legend>
		<div><acme:print value="${ratioAirlinesWithEmailAndPhone}" format="{0,number,#.##}"/></div>
	</fieldset>

	<!-- Ratio of active vs inactive aircrafts -->
	<fieldset>
		<legend><acme:print code="administrator.dashboard.form.label.aircraftStatusRatio"/></legend>
		<div><acme:print code="administrator.dashboard.form.label.active"/>: <acme:print value="${ratioActiveAircrafts}" format="{0,number,#.##}"/></div>
		<div><acme:print code="administrator.dashboard.form.label.inactive"/>: <acme:print value="${ratioInactiveAircrafts}" format="{0,number,#.##}"/></div>
	</fieldset>

	<!-- Ratio of reviews above 5 -->
	<fieldset>
		<legend><acme:print code="administrator.dashboard.form.label.ratioReviewsAboveFive"/></legend>
		<div><acme:print value="${ratioReviewsAboveFive}" format="{0,number,#.##}"/></div>
	</fieldset>

	<!-- Weekly review statistics -->
	<fieldset>
		<legend><acme:print code="administrator.dashboard.form.label.reviewStatsLast10Weeks"/></legend>
		<jstl:choose>
			<jstl:when test="${reviewStatsLast10Weeks.count() > 0}">
				<div>
					<acme:print code="administrator.dashboard.form.label.average"/>: ${reviewStatsLast10Weeks.average()}<br/>
					<acme:print code="administrator.dashboard.form.label.min"/>: ${reviewStatsLast10Weeks.min()}<br/>
					<acme:print code="administrator.dashboard.form.label.max"/>: ${reviewStatsLast10Weeks.max()}<br/>
					<acme:print code="administrator.dashboard.form.label.stddev"/>:
					<jstl:choose>
						<jstl:when test="${reviewStatsLast10Weeks.count() <= 1}">
							<acme:print code="administrator.dashboard.form.label.not-available"/>
						</jstl:when>
						<jstl:otherwise>
							<acme:print value="${reviewStatsLast10Weeks.stddev()}" format="{0,number,#.##}"/>
						</jstl:otherwise>
					</jstl:choose>
				</div>
			</jstl:when>
			<jstl:otherwise>
				<p><acme:print code="administrator.dashboard.form.label.no-data"/></p>
			</jstl:otherwise>
		</jstl:choose>
	</fieldset>

</acme:form>
