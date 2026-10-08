<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>

<%-- Business side also polls, so a second browser tab acting as the
     "user" sees notifications appear without the dashboard operator
     needing to explain what's happening behind the scenes. --%>
<meta http-equiv="refresh" content="5">

<h1><fmt:message key="dashboard.title" bundle="${msg}" /></h1>

<p class="now-serving">
    <fmt:message key="dashboard.nowServing" bundle="${msg}">
        <fmt:param value="${queue.currentServingPosition}" />
    </fmt:message>
</p>

<c:choose>
    <c:when test="${empty waitingTickets}">
        <p class="empty-state"><fmt:message key="dashboard.empty" bundle="${msg}" /></p>
    </c:when>
    <c:otherwise>
        <ul class="ticket-list">
            <c:forEach var="ticket" items="${waitingTickets}">
                <li>
                    <fmt:message key="dashboard.ticketRow" bundle="${msg}">
                        <fmt:param value="${ticket.position}" />
                        <fmt:param value="${ticket.userName}" />
                        <fmt:param value="${ticket.status}" />
                    </fmt:message>
                </li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>

<form action="${pageContext.request.contextPath}/callnext" method="post">
    <button type="submit" class="button primary">
        <fmt:message key="dashboard.callNext" bundle="${msg}" />
    </button>
</form>

<%@ include file="/WEB-INF/includes/footer.jspf" %>
