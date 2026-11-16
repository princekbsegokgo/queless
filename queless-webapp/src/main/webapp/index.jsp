<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>

<section class="portal-intro">
    <span class="eyebrow"><fmt:message key="home.portalEyebrow" bundle="${msg}"/></span>
    <h1><fmt:message key="home.welcome" bundle="${msg}"/></h1>
    <p><fmt:message key="home.portalDescription" bundle="${msg}"/></p>
</section>
<section class="portal-choices" aria-label="Choose how to use QueLess">
    <article class="portal-choice patient-choice">
        <div class="portal-choice-icon" aria-hidden="true">01</div>
        <h2><fmt:message key="home.patientTitle" bundle="${msg}"/></h2>
        <p><fmt:message key="home.patientDescription" bundle="${msg}"/></p>
        <a class="portal-button" href="${pageContext.request.contextPath}/joinqueue"><fmt:message key="home.patientButton" bundle="${msg}"/></a>
        <c:if test="${not empty sessionScope.myTicketToken}">
            <a class="portal-text-link" href="${pageContext.request.contextPath}/status?ticket=${sessionScope.myTicketToken}"><fmt:message key="home.trackTicket" bundle="${msg}"/></a>
        </c:if>
    </article>
    <article class="portal-choice staff-choice">
        <div class="portal-choice-icon" aria-hidden="true">02</div>
        <h2><fmt:message key="home.staffTitle" bundle="${msg}"/></h2>
        <p>Call the next patient, manage the queue, and show live statistics. No login needed for this demo.</p>
        <a class="portal-button outline" href="${pageContext.request.contextPath}/dashboard">Open staff dashboard</a>
    </article>
</section>
<p class="portal-disclaimer"><fmt:message key="home.demoNotice" bundle="${msg}"/></p>

<%@ include file="/WEB-INF/includes/footer.jspf" %>
