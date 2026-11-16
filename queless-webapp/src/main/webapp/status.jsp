<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>
<c:if test="${ticket.status ne 'SERVED'}">
    <meta http-equiv="refresh" content="4">
</c:if>

<h1><fmt:message key="status.title" bundle="${msg}" /></h1>

<c:choose>
    <c:when test="${ticket.status eq 'SERVED'}">
        <p class="status-message success"><fmt:message key="status.served" bundle="${msg}" /></p>
    </c:when>
    <c:when test="${ticket.status eq 'SERVING'}">
        <p class="status-message success"><fmt:message key="status.yourTurn" bundle="${msg}" /></p>
        <p class="status-position">
            <fmt:message key="status.position" bundle="${msg}">
                <fmt:param value="${ticket.position}" />
            </fmt:message>
        </p>
        <p class="auto-note"><fmt:message key="status.autoNote" bundle="${msg}" /></p>
    </c:when>
    <c:when test="${ticket.status eq 'CANCELLED'}">
        <p><fmt:message key="status.cancelled" bundle="${msg}" /></p>
    </c:when>
    <c:otherwise>
        <p class="status-position">
            <fmt:message key="status.position" bundle="${msg}">
                <fmt:param value="${ticket.position}" />
            </fmt:message>
        </p>
        <p>
            <fmt:message key="status.nowServing" bundle="${msg}">
                <fmt:param value="${nowServing}" />
            </fmt:message>
        </p>
        <p>
            <fmt:message key="status.estimatedWait" bundle="${msg}">
                <fmt:param value="${estimatedWaitMinutes}" />
            </fmt:message>
        </p>
        <c:if test="${not empty ticket.lastNotificationMessage}">
            <p class="notification-banner"><c:out value="${ticket.lastNotificationMessage}" /></p>
        </c:if>
        <p class="auto-note"><fmt:message key="status.autoNote" bundle="${msg}" /></p>
        <a class="button secondary" href="${pageContext.request.contextPath}/status?ticketId=${ticket.ticketId}">
            <fmt:message key="status.refresh" bundle="${msg}" />
        </a>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/includes/footer.jspf" %>
