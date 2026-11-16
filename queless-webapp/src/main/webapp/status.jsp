<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>
<c:if test="${ticket.status ne 'SERVED' and ticket.status ne 'CANCELLED'}">
    <meta http-equiv="refresh" content="6">
</c:if>

<section class="portal-intro compact-intro">
    <span class="eyebrow">Your private ticket</span>
    <h1>You're in the queue</h1>
    <p>This page checks for updates automatically. Keep your ticket link so you can return later.</p>
</section>
<article class="ticket-card">
    <div class="ticket-summary">
        <div>
            <span class="small-label">Department</span>
            <h2><c:out value="${queue.name}"/></h2>
        </div>
        <span class="queue-availability ${ticket.status eq 'SERVING' ? 'is-open' : ''}">
            <c:out value="${ticket.status}"/>
        </span>
    </div>
    <p class="ticket-number-label">Your ticket number</p>
    <p class="ticket-big-number">#${ticket.position}</p>
    <p class="ticket-name"><c:out value="${ticket.userName}"/></p>
    <c:choose>
        <c:when test="${ticket.status eq 'SERVING'}">
            <p class="portal-notice urgent" role="status">It's your turn! Please go to the service counter.</p>
        </c:when>
        <c:when test="${ticket.status eq 'SERVED'}">
            <p class="portal-notice" role="status">Your visit is marked complete. Thank you for using QueLess.</p>
        </c:when>
        <c:when test="${ticket.status eq 'CANCELLED'}">
            <p class="portal-notice" role="status">This ticket has been cancelled.</p>
        </c:when>
        <c:otherwise>
            <div class="ticket-progress">
                <div>
                    <span class="small-label">People ahead</span>
                    <strong>${peopleAhead}</strong>
                </div>
                <div>
                    <span class="small-label">Estimated wait</span>
                    <strong>${estimatedWaitMinutes} min</strong>
                </div>
            </div>
            <p class="ticket-status-note">Staff is currently serving ticket #${nowServing}. Keep this page open for your turn notification.</p>
            <c:if test="${not empty ticket.lastNotificationMessage}">
                <p class="portal-notice" role="status"><c:out value="${ticket.lastNotificationMessage}"/></p>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/cancel" class="cancel-form">
                <input type="hidden" name="_csrf" value="${csrfToken}">
                <input type="hidden" name="ticket" value="${ticket.accessToken}">
                <button class="cancel-button" type="submit">Leave this queue / cancel ticket</button>
            </form>
        </c:otherwise>
    </c:choose>
    <div class="ticket-actions">
        <a class="portal-button outline" href="${pageContext.request.contextPath}/status?ticket=${ticket.accessToken}">Refresh ticket status</a>
        <a class="portal-text-link" href="${pageContext.request.contextPath}/joinqueue">All departments</a>
    </div>
</article>
<p class="portal-disclaimer">Keep this link private: anyone with it can see your ticket details. Refreshing won't change your place in line. All tickets reset if the demo server restarts.</p>
<%@ include file="/WEB-INF/includes/footer.jspf" %>
