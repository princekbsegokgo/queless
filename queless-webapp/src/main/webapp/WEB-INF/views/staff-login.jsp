<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>

<section class="portal-intro staff-intro">
    <span class="eyebrow"><fmt:message key="staff.access" bundle="${msg}"/></span>
    <h1><fmt:message key="staff.portalTitle" bundle="${msg}"/></h1>
    <p><fmt:message key="staff.portalDescription" bundle="${msg}"/></p>
</section>
<div class="staff-login-card">
    <c:choose>
      <c:when test="${not staffConfigured}">
        <div class="portal-notice" role="alert">
            <strong><fmt:message key="staff.setupTitle" bundle="${msg}"/></strong>
            <p><fmt:message key="staff.setupPrelude" bundle="${msg}"/> <code>QUELESS_STAFF_PASSWORD</code> <fmt:message key="staff.setupPostlude" bundle="${msg}"/></p>
        </div>
      </c:when>
      <c:otherwise>
        <h2><fmt:message key="staff.signIn" bundle="${msg}"/></h2>
        <c:if test="${param.error eq '1'}"><p class="portal-error" role="alert"><fmt:message key="staff.badPassword" bundle="${msg}"/></p></c:if>
        <c:if test="${param.locked eq '1'}"><p class="portal-error" role="alert"><fmt:message key="staff.tooMany" bundle="${msg}"/></p></c:if>
        <form method="post" action="${pageContext.request.contextPath}/staff/login">
            <input type="hidden" name="_csrf" value="${csrfToken}">
            <label for="staffPassword"><fmt:message key="staff.password" bundle="${msg}"/></label>
            <input id="staffPassword" type="password" name="password" autocomplete="current-password" required>
            <button class="portal-button" type="submit"><fmt:message key="staff.openDashboard" bundle="${msg}"/></button>
        </form>
      </c:otherwise>
    </c:choose>
    <p class="supporting-text"><fmt:message key="staff.patientQuestion" bundle="${msg}"/> <a href="${pageContext.request.contextPath}/joinqueue"><fmt:message key="staff.joinInstead" bundle="${msg}"/></a>.</p>
</div>
<%@ include file="/WEB-INF/includes/footer.jspf" %>
