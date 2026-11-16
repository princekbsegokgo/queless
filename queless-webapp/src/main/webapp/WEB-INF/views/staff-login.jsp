<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>

<section class="portal-intro staff-intro">
    <span class="eyebrow">Staff access</span>
    <h1>QueLess Staff Portal</h1>
    <p>Clinic team members use this area to call patients and manage queues. Patients do not need to sign in.</p>
</section>
<div class="staff-login-card">
    <c:choose>
      <c:when test="${not staffConfigured}">
        <div class="portal-notice" role="alert">
            <strong>Staff login needs setting up.</strong>
            <p>To activate this portal, the project owner must add the <code>QUELESS_STAFF_PASSWORD</code> environment variable in Render and redeploy. No default password is provided.</p>
        </div>
      </c:when>
      <c:otherwise>
        <h2>Staff sign in</h2>
        <c:if test="${param.error eq '1'}"><p class="portal-error" role="alert">Incorrect password. Please try again.</p></c:if>
        <c:if test="${param.locked eq '1'}"><p class="portal-error" role="alert">Too many attempts in this session. Close the session and contact the project owner.</p></c:if>
        <form method="post" action="${pageContext.request.contextPath}/staff/login">
            <input type="hidden" name="_csrf" value="${csrfToken}">
            <label for="staffPassword">Staff password</label>
            <input id="staffPassword" type="password" name="password" autocomplete="current-password" required>
            <button class="portal-button" type="submit">Open staff dashboard</button>
        </form>
      </c:otherwise>
    </c:choose>
    <p class="supporting-text">Are you a patient? <a href="${pageContext.request.contextPath}/joinqueue">Join a queue instead</a>.</p>
</div>
<%@ include file="/WEB-INF/includes/footer.jspf" %>
