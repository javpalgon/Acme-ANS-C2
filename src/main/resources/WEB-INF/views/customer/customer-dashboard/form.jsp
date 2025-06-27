<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<!-- Últimos 5 destinos -->
	<fieldset>
		<legend><acme:print code="customer.dashboard.form.label.lastFiveDestinations"/></legend>
		<jstl:choose>
			<jstl:when test="${not empty lastFiveDestinations}">
				<jstl:forEach var="destination" items="${lastFiveDestinations}">
					<div>${destination}</div>
				</jstl:forEach>
			</jstl:when>
			<jstl:otherwise>
				<p><acme:print code="customer.dashboard.form.label.no-data"/></p>
			</jstl:otherwise>
		</jstl:choose>
	</fieldset>

	<!-- Dinero gastado el último año por moneda -->
	<fieldset>
		<legend><acme:print code="customer.dashboard.form.label.moneySpentLastYear"/></legend>
		<jstl:choose>
			<jstl:when test="${not empty totalMoneySpentLastYearByCurrency}">
				<jstl:forEach var="entry" items="${totalMoneySpentLastYearByCurrency}">
					<div>
						<strong>${entry.key}</strong>: <acme:print value="${entry.value}" format="{0,number,#.##}"/>
					</div>
				</jstl:forEach>
			</jstl:when>
			<jstl:otherwise>
				<p><acme:print code="customer.dashboard.form.label.no-data"/></p>
			</jstl:otherwise>
		</jstl:choose>
	</fieldset>


	<!-- Bookings por clase de viaje -->
	<fieldset>
		<legend><acme:print code="customer.dashboard.form.label.bookingsPerTravelClass"/></legend>
		<jstl:choose>
			<jstl:when test="${not empty bookingsPerTravelClass}">
				<jstl:forEach var="entry" items="${bookingsPerTravelClass}">
					<div>
						<strong>${entry.key}</strong>: <acme:print value="${entry.value}"/>
					</div>
				</jstl:forEach>
			</jstl:when>
			<jstl:otherwise>
				<p><acme:print code="customer.dashboard.form.label.no-data"/></p>
			</jstl:otherwise>
		</jstl:choose>
	</fieldset>

	<!-- Estadísticas del coste de reservas últimos 5 años -->
	<fieldset>
		<legend><acme:print code="customer.dashboard.form.label.costStatisticsLastFiveYears"/></legend>
		<jstl:choose>
			<jstl:when test="${not empty bookingCostStatsLastFiveYears}">
			     <jstl:forEach var="entry" items="${bookingCostStatsLastFiveYears}">
					<div>
						<strong>${entry.key}</strong><br/>
						<acme:print code="customer.dashboard.form.label.averageCost"/>: ${entry.value.average()}<br/>
						<acme:print code="customer.dashboard.form.label.minCost"/>: ${entry.value.min()}<br/>
						<acme:print code="customer.dashboard.form.label.maxCost"/>: ${entry.value.max()}<br/>
						<acme:print code="customer.dashboard.form.label.stddevCost"/>: 
						<jstl:choose>
							<jstl:when test="${entry.value.count() <= 1}">
								<acme:print code="customer.dashboard.form.label.not-available"/>
							</jstl:when>
							<jstl:otherwise>
								<acme:print value="${entry.value.stddev()}" format="{0,number,#.##}"/>
							</jstl:otherwise>
						</jstl:choose>
					</div>
					<br/>
				</jstl:forEach>
			</jstl:when>
			<jstl:otherwise>
				<p><acme:print code="customer.dashboard.form.label.no-data"/></p>
			</jstl:otherwise>
		</jstl:choose>
	</fieldset>

	<!-- Estadísticas de pasajeros por reserva -->
	<fieldset>
		<legend><acme:print code="customer.dashboard.form.label.passengersStats"/></legend>
		<jstl:choose>
			<jstl:when test="${passengersStatistics.count() > 0}">
				<div>
					<acme:print code="customer.dashboard.form.label.passengersAverage"/>: ${passengersStatistics.average()}<br/>
					<acme:print code="customer.dashboard.form.label.passengersMin"/>: ${passengersStatistics.min()}<br/>
					<acme:print code="customer.dashboard.form.label.passengersMax"/>: ${passengersStatistics.max()}<br/>
					<acme:print code="customer.dashboard.form.label.passengersStddev"/>:
					<jstl:choose>
						<jstl:when test="${passengersStatistics.count() <= 1}">
							<acme:print code="customer.dashboard.form.label.not-available"/>
						</jstl:when>
						<jstl:otherwise>
							<acme:print value="${passengersStatistics.stddev()}" format="{0,number,#.##}"/>
						</jstl:otherwise>
					</jstl:choose>
				</div>
			</jstl:when>
			<jstl:otherwise>
				<p><acme:print code="customer.dashboard.form.label.no-data"/></p>
			</jstl:otherwise>
		</jstl:choose>
	</fieldset>

</acme:form>
