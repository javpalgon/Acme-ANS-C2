<%--
- menu.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.favourite-link" action="http://www.example.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.student1-favourite-link" action="https://www.ubereats.com/es-en"/>
			<acme:menu-suboption code="master.menu.anonymous.student2-favourite-link" action="https://lapalabradeldia.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.student3-favourite-link" action="https://www.realbetisbalompie.es/"/>
			<acme:menu-suboption code="master.menu.anonymous.student4-favourite-link" action="https://premierpadel.com/es/tournaments"/>
			<acme:menu-suboption code="master.menu.anonymous.student5-favourite-link" action="https://www.oscars.org/"/>
		</acme:menu-option>
		<acme:menu-option code="master.menu.company" access="isAnonymous()" action="/any/system/company"/>
		<acme:menu-option code="master.menu.products" access="isAnonymous()"/>
		<acme:menu-option code="master.menu.news" access="isAnonymous()"/>
		<acme:menu-option code="master.menu.events" access="isAnonymous()"/>
		<acme:menu-option code="master.menu.contact" access="isAnonymous()"/>
		<acme:menu-option code="master.menu.any" access="isAnonymous()" action="/any/weather-dashboard/show"/>

		
		<acme:menu-option code="master.menu.manager" access="hasRealm('Manager')">
			<acme:menu-suboption code="master.menu.manager.list-my-flights" action="/manager/flight/list"/>
			<acme:menu-suboption code="master.menu.manager.dashboard" action="/manager/manager-dashboard/show"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.customer" access="hasRealm('Customer')">
			<acme:menu-suboption code="master.menu.customer.list-my-bookings" action="/customer/booking/list"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.agent" access="hasRealm('AssistanceAgent')">
			<acme:menu-suboption code="master.menu.agent.list-completed-claims" action="/assistance-agent/claim/list-completed"/>
			<acme:menu-suboption code="master.menu.agent.list-pending-claims" action="/assistance-agent/claim/list-pending"/>
		</acme:menu-option>
		

		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
			<acme:menu-suboption code="master.menu.technician.list-my-maintenance-records" action="/technician/maintenance-record/list"/>
		</acme:menu-option>
		

		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">
			<acme:menu-suboption code="master.menu.administrator.list-airlines" action="/administrator/airline/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-airports" action="/administrator/airport/list"/>	
			<acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-aircrafts" action="/administrator/aircraft/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.provider" access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer" access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>
		

		<acme:menu-option code="master.menu.member" access="hasRealm('Member')">
		    <acme:menu-suboption code="master.menu.member.list-completed-assignments" action="/member/assignment/list-completed"/>
		    <acme:menu-suboption code="master.menu.member.list-planned-assignments" action="/member/assignment/list-planned"/>
		</acme:menu-option>
		

	</acme:menu-left>

	<acme:menu-right>		
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile" action="/authenticated/user-account/update"/>
			<acme:menu-suboption code="master.menu.user-account.become-provider" action="/authenticated/provider/create" access="!hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.provider-profile" action="/authenticated/provider/update" access="hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.become-consumer" action="/authenticated/consumer/create" access="!hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.consumer-profile" action="/authenticated/consumer/update" access="hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-manager" action="/authenticated/manager/create" access="!hasRealm('Manager')"/>
			<acme:menu-suboption code="master.menu.user-account.manager-profile" action="/authenticated/manager/update" access="hasRealm('Manager')"/>
		</acme:menu-option>
	</acme:menu-right>
</acme:menu-bar>

