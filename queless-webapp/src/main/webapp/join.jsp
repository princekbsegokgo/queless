<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>

<h1><fmt:message key="join.title" bundle="${msg}" /></h1>
<c:if test="${param.paused eq '1'}">
    <p class="notification-banner" role="status"><fmt:message key="join.paused" bundle="${msg}" /></p>
</c:if>

<c:forEach var="queue" items="${queues}">
    <div class="card">
        <c:choose>
            <c:when test="${not queue.open}">
                <p class="queue-name"><c:out value="${queue.name}" /></p>
                <p><fmt:message key="join.paused" bundle="${msg}" /></p>
            </c:when>
            <c:otherwise>
                <form action="${pageContext.request.contextPath}/joinqueue" method="post" class="join-form">
                    <input type="hidden" name="queueId" value="${queue.queueId}" />
                    <span class="queue-name"><c:out value="${queue.name}" /></span>
                    <input type="text" name="userName" maxlength="80"
                           placeholder="<fmt:message key="join.namePlaceholder" bundle="${msg}" />"
                           required="required" />
                    <button type="submit" class="button primary">
                        <fmt:message key="join.button" bundle="${msg}" />
                    </button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</c:forEach>

<%@ include file="/WEB-INF/includes/footer.jspf" %>
