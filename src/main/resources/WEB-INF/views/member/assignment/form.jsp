<%@page language="java"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="true">
    <!-- Assignment Info -->
    <acme:input-textbox code="member.assignment.form.label.role" path="role"/>
    <acme:input-moment code="member.assignment.form.label.lastUpdate" path="lastUpdate"/>
    <acme:input-textbox code="member.assignment.form.label.status" path="status"/>
    <acme:input-textarea code="member.assignment.form.label.remarks" path="remarks"/>

    <!-- Flight Leg Info -->
    <h3><acme:print code="member.assignment.form.label.flightInfo"/></h3>
    <acme:input-textbox code="member.assignment.form.label.flightNumber" path="flightNumber"/>
    <acme:input-moment code="member.assignment.form.label.departure" path="departure"/>
    <acme:input-moment code="member.assignment.form.label.arrival" path="arrival"/>
    <acme:input-textbox code="member.assignment.form.label.status" path="legStatus"/>
    <acme:input-textbox code="member.assignment.form.label.departureAirport" path="departureAirport"/>
    <acme:input-textbox code="member.assignment.form.label.arrivalAirport" path="arrivalAirport"/>
    <acme:input-textbox code="member.assignment.form.label.aircraft" path="aircraft"/>

    <!-- Member Info -->
    <h3><acme:print code="member.assignment.form.label.memberInfo"/></h3>
    <acme:input-textbox code="member.assignment.form.label.memberName" path="memberName"/>
    <acme:input-textbox code="member.assignment.form.label.employeeCode" path="employeeCode"/>
    <acme:input-textbox code="member.assignment.form.label.phoneNumber" path="phoneNumber"/>
    <acme:input-textarea code="member.assignment.form.label.languageSkills" path="languageSkills"/>
    <acme:input-integer code="member.assignment.form.label.yearsOfExperience" path="yearsOfExperience"/>
    <acme:input-money code="member.assignment.form.label.salary" path="salary"/>
    <acme:input-textbox code="member.assignment.form.label.availabilityStatus" path="availabilityStatus"/>

</acme:form>