<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>

<h1><fmt:message key="join.title" bundle="${msg}" /></h1>

<c:forEach var="queue" items="${queues}">
    <div class="card">
        <form action="${pageContext.request.contextPath}/joinqueue" method="post" class="join-form">
            <input type="hidden" name="queueId" value="${queue.queueId}" />
            <span class="queue-name">${queue.name}</span>
            <input
                type="text"
                name="userName"
                placeholder="<fmt:message key="join.namePlaceholder" bundle="${msg}" />"
                required="required" />
            <button type="submit" class="button primary">
                <fmt:message key="join.button" bundle="${msg}" />
            </button>
        </form>
    </div>
</c:forEach>

<%@ include file="/WEB-INF/includes/footer.jspf" %>
