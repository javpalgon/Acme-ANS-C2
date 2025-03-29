<%@page language="java"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <!-- Common fields for all views -->
    <!-- Role Field -->
    <jstl:choose>
        <jstl:when test="${_command == 'show'}">
            <acme:input-textbox code="member.assignment.form.label.role" path="role" />
        </jstl:when>
        <jstl:otherwise>
            <acme:input-select code="member.assignment.form.label.role" path="role" choices="${roles}"/>
        </jstl:otherwise>
    </jstl:choose>

    <!-- Status Field -->
    <jstl:choose>
        <jstl:when test="${_command == 'show'}">
            <acme:input-textbox code="member.assignment.form.label.status" path="status"/>
        </jstl:when>
        <jstl:otherwise>
            <acme:input-select code="member.assignment.form.label.status" path="status" choices="${statuses}"/>
        </jstl:otherwise>
    </jstl:choose>

    <acme:input-textarea code="member.assignment.form.label.remarks" path="remarks"/>
   
    <acme:input-textbox code="member.assignment.form.label.lastUpdate" path="lastUpdate" readonly = "true"/>


    <!-- Selectable fields for create/update -->
    <jstl:if test="${_command == 'create'}">
        <acme:input-select code="member.assignment.form.label.leg" path="leg" choices="${legs}"/>

        <acme:input-select code="member.assignment.form.label.member" path="member" choices="${members}"/>
        
        <acme:input-checkbox code="member.assignment.form.label.isDraftMode" path="isDraftMode"/>
    </jstl:if>
    
    <!-- Display-only fields for show view -->
    <jstl:if test="${_command == 'show'}">
        <!-- Flight Leg Info -->
        <h3><acme:print code="member.assignment.form.label.flightInfo"/></h3>
        <acme:input-textbox code="member.assignment.form.label.flightNumber" path="flightNumber" />
        
        <acme:input-moment code="member.assignment.form.label.departure" path="departure" />
        
        <acme:input-moment code="member.assignment.form.label.arrival" path="arrival" />
        
        <acme:input-textbox code="member.assignment.form.label.status" path="legStatus" />
        
        <acme:input-textbox code="member.assignment.form.label.departureAirport" path="departureAirport" />
        
        <acme:input-textbox code="member.assignment.form.label.arrivalAirport" path="arrivalAirport" />
        
        <acme:input-textbox code="member.assignment.form.label.aircraft" path="aircraft" />

        <!-- Member Info -->
        <h3><acme:print code="member.assignment.form.label.memberInfo"/></h3>
        <acme:input-textbox code="member.assignment.form.label.memberName" path="memberName" />
        
        <acme:input-textbox code="member.assignment.form.label.employeeCode" path="employeeCode" />
        
        <acme:input-textbox code="member.assignment.form.label.phoneNumber" path="phoneNumber" />
        
        <acme:input-textarea code="member.assignment.form.label.languageSkills" path="languageSkills" />
        
        <acme:input-integer code="member.assignment.form.label.yearsOfExperience" path="yearsOfExperience" />
        
        <acme:input-money code="member.assignment.form.label.salary" path="salary" />
        
        <acme:input-textbox code="member.assignment.form.label.availabilityStatus" path="availabilityStatus" />
    </jstl:if>
    
    <!-- Conditional buttons -->
    <jstl:choose>
        <jstl:when test="${acme:anyOf(_command, 'show|update')}">
            <acme:submit code="member.assignment.form.button.update" action="/member/assignment/update"/>
        </jstl:when>
        <jstl:when test="${_command == 'create'}">
            <acme:submit code="member.assignment.form.button.create" action="/member/assignment/create"/>
        </jstl:when>
    </jstl:choose>
</acme:form>
