<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>
<c:if test="${ticket.status ne 'SERVED' and ticket.status ne 'CANCELLED'}">
    <meta http-equiv="refresh" content="6">
</c:if>

<section class="portal-intro compact-intro">
    <span class="eyebrow"><fmt:message key="status.privateTicket" bundle="${msg}"/></span>
    <h1><fmt:message key="status.inQueue" bundle="${msg}"/></h1>
    <p><fmt:message key="status.trackingNotice" bundle="${msg}"/></p>
</section>
<article class="ticket-card">
    <div class="ticket-summary">
        <div>
            <span class="small-label"><fmt:message key="join.department" bundle="${msg}"/></span>
            <h2><c:out value="${queue.name}"/></h2>
        </div>
        <span class="queue-availability ${ticket.status eq 'SERVING' ? 'is-open' : ''}">
            <c:out value="${ticket.status}"/>
        </span>
    </div>
    <p class="ticket-number-label"><fmt:message key="status.ticketNumber" bundle="${msg}"/></p>
    <p class="ticket-big-number">#${ticket.position}</p>
    <p class="ticket-name"><c:out value="${ticket.userName}"/></p>
    <c:choose>
        <c:when test="${ticket.status eq 'SERVING'}">
            <p class="portal-notice urgent" role="status"><fmt:message key="status.turnNotice" bundle="${msg}"/></p>
        </c:when>
        <c:when test="${ticket.status eq 'SERVED'}">
            <p class="portal-notice" role="status"><fmt:message key="status.completeNotice" bundle="${msg}"/></p>
        </c:when>
        <c:when test="${ticket.status eq 'CANCELLED'}">
            <p class="portal-notice" role="status"><fmt:message key="status.cancelNotice" bundle="${msg}"/></p>
        </c:when>
        <c:otherwise>
            <div class="ticket-progress">
                <div>
                    <span class="small-label"><fmt:message key="status.peopleAhead" bundle="${msg}"/></span>
                    <strong>${peopleAhead}</strong>
                </div>
                <div>
                    <span class="small-label"><fmt:message key="status.waitLabel" bundle="${msg}"/></span>
                    <strong>${estimatedWaitMinutes} min</strong>
                </div>
            </div>
            <p class="ticket-status-note">Staff is currently serving ticket #${nowServing}. <fmt:message key="status.keepOpen" bundle="${msg}"/></p>
            <c:if test="${not empty ticket.lastNotificationMessage}">
                <p class="portal-notice" role="status"><c:out value="${ticket.lastNotificationMessage}"/></p>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/cancel" class="cancel-form">
                <input type="hidden" name="_csrf" value="${csrfToken}">
                <input type="hidden" name="ticket" value="${ticket.accessToken}">
                <button class="cancel-button" type="submit"><fmt:message key="status.cancelButton" bundle="${msg}"/></button>
            </form>
        </c:otherwise>
    </c:choose>
    <div class="ticket-actions">
        <a class="portal-button outline" href="${pageContext.request.contextPath}/status?ticket=${ticket.accessToken}"><fmt:message key="status.refreshTicket" bundle="${msg}"/></a>
        <a class="portal-text-link" href="${pageContext.request.contextPath}/joinqueue"><fmt:message key="status.allDepartments" bundle="${msg}"/></a>
    </div>
</article>
<p class="portal-disclaimer"><fmt:message key="status.disclaimer" bundle="${msg}"/></p>
<%@ include file="/WEB-INF/includes/footer.jspf" %>
