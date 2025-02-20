<%--
- footer.jsp
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

<acme:footer-panel>
	<acme:footer-subpanel code="master.footer.title.about">
		<acme:footer-option icon="fa fa-building" code="master.footer.label.company" action="/any/system/company"/>
		<acme:footer-option icon="fa fa-file" code="master.footer.label.license" action="/any/system/license"/>
	</acme:footer-subpanel>

	<acme:footer-subpanel code="master.footer.title.social">
		<acme:print var="$linkedin$url" code="master.footer.url.linkedin"/>
		<acme:footer-option icon="fab fa-linkedin" code="master.footer.label.linked-in" action="${$linkedin$url}" newTab="true"/>
		<acme:print var="$twitter$url" code="master.footer.url.twitter"/>
		<acme:footer-option icon="fab fa-twitter" code="master.footer.label.twitter" action="${$twitter$url}" newTab="true"/>
	</acme:footer-subpanel>

	<acme:footer-subpanel code="master.footer.title.languages">
		<acme:footer-option icon="fa fa-language" code="master.footer.label.english" action="/?locale=en"/>
		<acme:footer-option icon="fa fa-language" code="master.footer.label.spanish" action="/?locale=es"/>
	</acme:footer-subpanel>
	
	<acme:footer-subpanel code="master.footer.title.airlines">
    	<acme:footer-option icon="fa fa-plane" code="master.footer.label.ryanair" action="https://www.ryanair.com/es/es"/>
    	<acme:footer-option icon="fa fa-plane" code="master.footer.label.iberia" action="https://www.iberia.com/es/?cq_src=google_ads&cq_cmp=206334386&cq_con=11273681546&cq_term=iberia&cq_med=&cq_plac=&cq_net=g&cq_plt=gp&esl-k=google-ads|ng|c679405423166|me|kiberia|p|t|dc|a11273681546|g206334386&utm_source=google&utm_medium=cpc&utm_campaign=search-206334386&gad_source=1&gclid=Cj0KCQiAwtu9BhC8ARIsAI9JHallvJDiWJSjqevbILetYC-9gluqNv6FTTBrkfE2KzoGV-jBQqm48FAaAj_MEALw_wcB"/>
	</acme:footer-subpanel>

	<acme:footer-logo logo="images/logo.png" alt="master.company.name">
		<acme:footer-copyright code="master.company.name"/>
	</acme:footer-logo>
</acme:footer-panel>
