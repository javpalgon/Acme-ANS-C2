<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
    <acme:list-column code="weather.dashboard.list.label.city" path="city"/>
    <acme:list-column code="weather.dashboard.list.label.timestamp" path="timestamp"/>
    <acme:list-column code="weather.dashboard.list.label.weatherMain" path="weatherMain"/>
    <acme:list-column code="weather.dashboard.list.label.weatherDescription" path="weatherDescription"/>
    <acme:list-column code="weather.dashboard.list.label.visibility" path="visibility"/>
    <acme:list-column code="weather.dashboard.list.label.windSpeed" path="windSpeed"/>
    <acme:list-column code="weather.dashboard.list.label.isBadWeather" path="isBadWeather"/>
</acme:list>
