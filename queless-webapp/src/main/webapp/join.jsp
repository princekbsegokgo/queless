<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>

<section class="portal-intro compact-intro">
    <span class="eyebrow">Patient portal</span>
    <h1>Join a queue</h1>
    <p>Choose the department you need, enter your name and get a ticket you can track on your phone.</p>
</section>
<c:if test="${param.paused eq '1'}">
    <p class="portal-notice" role="alert">That queue is paused right now. You can try another department or check back later.</p>
</c:if>
<c:if test="${param.invalid eq '1'}">
    <p class="portal-error" role="alert">Please enter your name (up to 80 characters).</p>
</c:if>
<c:if test="${param.notfound eq '1'}">
    <p class="portal-notice" role="status">Ticket not found. It may have expired after a server restart. Please join a new queue.</p>
</c:if>

<div class="patient-queues">
    <c:forEach var="queue" items="${queues}">
        <article class="patient-queue-card">
            <div class="patient-queue-heading">
                <div>
                    <span class="small-label">Department</span>
                    <h2><c:out value="${queue.name}"/></h2>
                </div>
                <span class="queue-availability ${queue.open ? 'is-open' : 'is-paused'}">${queue.open ? 'Open' : 'Paused'}</span>
            </div>
            <p class="queue-counter"><strong>${waitingByQueue[queue.queueId]}</strong> people currently waiting</p>
            <c:choose>
                <c:when test="${queue.open}">
                    <form action="${pageContext.request.contextPath}/joinqueue" method="post" class="patient-join-form">
                        <input type="hidden" name="queueId" value="${queue.queueId}">
                        <input type="hidden" name="_csrf" value="${csrfToken}">
                        <label for="name-${queue.queueId}">Your name</label>
                        <input id="name-${queue.queueId}" type="text" name="userName" maxlength="80"
                            autocomplete="name" placeholder="Enter your name" required>
                        <button class="portal-button" type="submit">Get my queue ticket</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <p class="queue-help">This queue is paused by staff. New tickets are not being issued.</p>
                </c:otherwise>
            </c:choose>
        </article>
    </c:forEach>
</div>
<p class="portal-disclaimer">Queue times are estimates, not guaranteed appointments. No medical or identity information is needed for this demo.</p>
<%@ include file="/WEB-INF/includes/footer.jspf" %>
