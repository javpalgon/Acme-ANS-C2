<%@page language="java"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-moment code="technician.maintenance-record.form.label.maintenanceTimestamp" path="maintenanceTimestamp"/>

    <acme:input-select code="technician.maintenance-record.form.label.maintenanceStatus" path="maintenanceStatus" choices="${maintenanceRecordStatus}"/>

    <acme:input-moment code="technician.maintenance-record.form.label.nextInspectionDate" path="nextInspectionDate"/>
   
    <acme:input-money code="technician.maintenance-record.form.label.estimatedCost" path="estimatedCost"/>
    
    <acme:input-textbox code="technician.maintenance-record.form.label.notes" path="notes"/>
    
    <acme:input-checkbox code="technician.maintenance-record.form.label.isDraftMode" path="isDraftMode"/>
</acme:form>