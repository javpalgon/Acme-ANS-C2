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


<jstl:if test="${service != null}">
    <div class="panel-body" style="margin: 1em 0; text-align: center; padding: 1.5em; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
        
        <!-- Contenedor de imagen con tamaño fijo -->
        <div style="width: 200px; height: 150px; margin: 0 auto; display: flex; 
                   align-items: center; justify-content: center; border: 2px solid #f0f0f0; 
                   border-radius: 4px; overflow: hidden; background: #fafafa;">
            <img src="${service.pictureLink}" 
                 alt="<acme:print code='service.image.alt' value='${service.name}'/>"
                 style="max-width: 100%; max-height: 100%; object-fit: contain;"/>
        </div>
        
        <!-- Contenido textual con mensajes internacionalizados -->
        <div style="margin-top: 1em;">
            <!-- Nombre -->
            <div style="font-size: 1.1rem; font-weight: 600; color: #2c3e50; margin-bottom: 0.5em;">
                <acme:print code="service.name.label"/>: 
                <span style="font-weight: 700;"><acme:print value="${service.name}"/></span>
            </div>
            
            <!-- Código promocional -->
            <div style="display: inline-block; background: #f8f9fa; border-radius: 16px; 
                     padding: 0.3em 1em; margin: 0.3em 0; font-size: 0.85rem; color: #6c757d; 
                     border: 1px dashed #ced4da;">
                <acme:print code="service.promotion.label"/>: 
                <strong style="color: #e74c3c;"><acme:print value="${service.promotionCode}"/></strong>
            </div>
            
            <!-- Descuento -->
            <div style="font-size: 1rem; color: #27ae60; font-weight: 500; margin: 0.8em 0;">
                <acme:print code="service.discount.label"/>: 
                <strong><acme:print value="${service.discount.amount}"/> <acme:print value="${service.discount.currency}"/></strong>
            </div>
            
            <!-- Duración -->
            <div style="font-size: 0.9rem; color: #7f8c8d;">
                <acme:print code="service.duration.label"/>: 
                <strong><acme:print value="${service.averageDwellTime}"/> <acme:print code="time.minutes"/></strong>
            </div>
        </div>
    </div>
</jstl:if>