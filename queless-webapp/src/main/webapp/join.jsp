<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>

<section class="portal-intro compact-intro">
    <span class="eyebrow"><fmt:message key="join.patientPortal" bundle="${msg}"/></span>
    <h1><fmt:message key="join.portalTitle" bundle="${msg}"/></h1>
    <p><fmt:message key="join.portalDescription" bundle="${msg}"/></p>
</section>
<c:if test="${param.paused eq '1'}">
    <p class="portal-notice" role="alert"><fmt:message key="join.pausedNotice" bundle="${msg}"/></p>
</c:if>
<c:if test="${param.invalid eq '1'}">
    <p class="portal-error" role="alert"><fmt:message key="join.invalidName" bundle="${msg}"/></p>
</c:if>
<c:if test="${param.notfound eq '1'}">
    <p class="portal-notice" role="status"><fmt:message key="join.notFound" bundle="${msg}"/></p>
</c:if>

<div class="patient-queues">
    <c:forEach var="queue" items="${queues}">
        <article class="patient-queue-card">
            <div class="patient-queue-heading">
                <div>
                    <span class="small-label"><fmt:message key="join.department" bundle="${msg}"/></span>
                    <h2><c:out value="${queue.name}"/></h2>
                </div>
                <span class="queue-availability ${queue.open ? 'is-open' : 'is-paused'}">${queue.open ? 'Open' : 'Paused'}</span>
            </div>
            <p class="queue-counter"><strong>${waitingByQueue[queue.queueId]}</strong> <fmt:message key="join.peopleWaiting" bundle="${msg}"/></p>
            <c:choose>
                <c:when test="${queue.open}">
                    <form action="${pageContext.request.contextPath}/joinqueue" method="post" class="patient-join-form">
                        <input type="hidden" name="queueId" value="${queue.queueId}">
                        <input type="hidden" name="_csrf" value="${csrfToken}">
                        <label for="name-${queue.queueId}"><fmt:message key="join.yourName" bundle="${msg}"/></label>
                        <input id="name-${queue.queueId}" type="text" name="userName" maxlength="80"
                            autocomplete="name" placeholder="Enter your name" required>
                        <button class="portal-button" type="submit"><fmt:message key="join.getTicket" bundle="${msg}"/></button>
                    </form>
                </c:when>
                <c:otherwise>
                    <p class="queue-help"><fmt:message key="join.pausedDetails" bundle="${msg}"/></p>
                </c:otherwise>
            </c:choose>
        </article>
    </c:forEach>
</div>
<p class="portal-disclaimer"><fmt:message key="join.disclaimer" bundle="${msg}"/></p>
<%@ include file="/WEB-INF/includes/footer.jspf" %>
