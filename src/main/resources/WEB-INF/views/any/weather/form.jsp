<%@page language="java"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
   	<acme:input-textbox code="weather.dashboard.list.label.city" path="city" readonly="true"/>
    <acme:input-moment code="weather.dashboard.list.label.timestamp" path="timestamp" readonly="true"/>
    <acme:input-textbox code="weather.dashboard.list.label.weatherMain" path="weatherMain" readonly="true"/>
    <acme:input-textbox code="weather.dashboard.list.label.weatherDescription" path="weatherDescription" readonly="true"/>
    <acme:input-textbox code="weather.dashboard.list.label.visibility" path="visibility" readonly="true"/>
    <acme:input-textbox code="weather.dashboard.list.label.windSpeed" path="windSpeed" readonly="true"/>
    <acme:input-checkbox code="weather.dashboard.list.label.isBadWeather" path="isBadWeather" readonly="true"/>
</acme:form>