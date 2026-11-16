<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>

<section class="portal-intro">
    <span class="eyebrow">A smarter way to wait</span>
    <h1>Welcome to QueLess</h1>
    <p>Join a clinic queue from your phone, see your position, and know when it is your turn.</p>
</section>
<section class="portal-choices" aria-label="Choose how to use QueLess">
    <article class="portal-choice patient-choice">
        <div class="portal-choice-icon" aria-hidden="true">01</div>
        <h2>I'm a patient</h2>
        <p>Choose a service, join a line, and track your ticket while you wait.</p>
        <a class="portal-button" href="${pageContext.request.contextPath}/joinqueue">Join as a patient</a>
        <c:if test="${not empty sessionScope.myTicketToken}">
            <a class="portal-text-link" href="${pageContext.request.contextPath}/status?ticket=${sessionScope.myTicketToken}">Track my current ticket</a>
        </c:if>
    </article>
    <article class="portal-choice staff-choice">
        <div class="portal-choice-icon" aria-hidden="true">02</div>
        <h2>I'm clinic staff</h2>
        <p>Sign in to call the next patient, complete a visit, and manage each queue.</p>
        <a class="portal-button outline" href="${pageContext.request.contextPath}/staff/login">Staff sign in</a>
    </article>
</section>
<p class="portal-disclaimer">QueLess currently runs as a demonstration. Do not enter medical details or emergencies here. For an emergency, contact the clinic or emergency services directly.</p>

<%@ include file="/WEB-INF/includes/footer.jspf" %>
