<%@page language="java"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <!-- Common fields for all views -->
<acme:input-select code="member.assignment.form.label.role" path="role" choices="${role}" readonly="${assignment.isDraftMode}"/>

<acme:input-select code="member.assignment.form.label.status" path="status" choices="${status}" readonly="${assignment.isDraftMode}"/>

<acme:input-textbox code="member.assignment.form.label.remarks" path="remarks" readonly="${assignment.isDraftMode}"/>

<acme:input-select code="member.assignment.form.label.leg" path="leg" choices="${legs}" readonly="${assignment.isDraftMode}"/>

<acme:input-select code="member.assignment.form.label.member" path="member" choices="${members}" readonly="${assignment.isDraftMode}"/>



    
    <!-- Display-only fields for show view -->
    <jstl:if test="${_command == 'show'}" >
    	<acme:input-moment code="member.assignment.form.label.lastUpdate" path="lastUpdate" readonly="true"/>
        <!-- Flight Leg Info -->
        <h3><acme:print code="member.assignment.form.label.flightInfo"/></h3>
        <acme:input-textbox code="member.assignment.form.label.flightNumber" path="flightNumber" readonly="true"/>
        
        <acme:input-moment code="member.assignment.form.label.departure" path="departure" readonly="true"/>
        
        <acme:input-moment code="member.assignment.form.label.arrival" path="arrival" readonly="true"/>
        
        <acme:input-textbox code="member.assignment.form.label.status" path="legStatus" readonly="true"/>
        
        <acme:input-textbox code="member.assignment.form.label.departureAirport" path="departureAirport" readonly="true"/>
        
        <acme:input-textbox code="member.assignment.form.label.arrivalAirport" path="arrivalAirport" readonly="true"/>
        
        <acme:input-textbox code="member.assignment.form.label.aircraft" path="aircraft" readonly="true"/>

        <!-- Member Info -->
        <h3><acme:print code="member.assignment.form.label.memberInfo"/></h3>
        <acme:input-textbox code="member.assignment.form.label.memberName" path="memberName" readonly="true"/>
        
        <acme:input-textbox code="member.assignment.form.label.employeeCode" path="employeeCode" readonly="true"/>
        
        <acme:input-textbox code="member.assignment.form.label.phoneNumber" path="phoneNumber" readonly="true"/>
        
        <acme:input-textarea code="member.assignment.form.label.languageSkills" path="languageSkills" readonly="true"/>
        
        <acme:input-money code="member.assignment.form.label.salary" path="salary" readonly="true"/>
        
        <acme:input-textbox code="member.assignment.form.label.yearsOfExperience" path="yearsOfExperience" readonly="true"/>
        
        <acme:input-textbox code="member.assignment.form.label.availabilityStatus" path="availabilityStatus" readonly="true"/>
    </jstl:if>
    
    <!-- Conditional buttons -->
	<jstl:choose>
	    <jstl:when test="${acme:anyOf(_command, 'show|update|publish|delete') && isDraftMode == true}">
	        <acme:submit code="member.assignment.form.button.update" action="/member/assignment/update"/>
	        <acme:submit code="member.assignment.form.button.publish" action="/member/assignment/publish"/>
	        <acme:submit code="member.assignment.form.button.delete" action="/member/assignment/delete"/>
	        <acme:button code="member.assignment.form.button.activity-log" action="/member/activity-log/list?masterId=${id}"/>
	    </jstl:when>
	    
	    <jstl:when test="${_command == 'show' && isDraftMode == false}">
 			<acme:button code="member.assignment.form.button.activity-log" action="/member/activity-log/list?masterId=${id}"/>			
 		</jstl:when>
	    
	    <jstl:when test="${_command == 'create'}">
	        <acme:submit code="member.assignment.form.button.create" action="/member/assignment/create"/>
	    </jstl:when>
	</jstl:choose>
</acme:form>


