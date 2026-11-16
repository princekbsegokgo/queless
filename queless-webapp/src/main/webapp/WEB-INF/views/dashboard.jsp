<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:if test="${empty sessionScope.lang}">
    <c:set var="lang" value="en" scope="session" />
</c:if>
<fmt:setLocale value="${sessionScope.lang}" />
<fmt:setBundle basename="messages" var="msg" />
<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="refresh" content="10">
    <title><fmt:message key="dashboard.fullTitle" bundle="${msg}" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body class="business-dashboard-page">
<main class="biz-dashboard">
    <header class="dashboard-topbar">
        <h1><fmt:message key="dashboard.fullTitle" bundle="${msg}" /></h1>
        <div class="dashboard-header-actions">
            <nav class="dashboard-nav" aria-label="Dashboard navigation">
                <a class="dashboard-nav-link" href="${pageContext.request.contextPath}/"><fmt:message key="nav.home" bundle="${msg}"/></a>
                <a class="dashboard-nav-link" href="${pageContext.request.contextPath}/joinqueue" ><fmt:message key="nav.customerDashboard" bundle="${msg}"/></a>
                <a class="dashboard-nav-link is-active" href="${pageContext.request.contextPath}/dashboard" aria-current="page"><fmt:message key="nav.staffDashboard" bundle="${msg}"/></a>
            </nav>
            <div class="dashboard-language" aria-label="Language">
                <a href="${pageContext.request.contextPath}/language?lang=en&amp;returnTo=/dashboard"
                   class="${sessionScope.lang eq 'en' ? 'selected' : ''}" lang="en">EN</a>
                <span aria-hidden="true">/</span>
                <a href="${pageContext.request.contextPath}/language?lang=tn&amp;returnTo=/dashboard"
                   class="${sessionScope.lang eq 'tn' ? 'selected' : ''}" lang="tn">TSW</a>
            </div>
            <span class="dashboard-clinic" title="Clinic currently configured in this demo">
                <c:out value="${business.name}" />
            </span>
        </div>
    </header>

    <section class="dashboard-metrics" aria-label="Queue metrics">
        <div class="dashboard-metric metric-blue">
            <span class="metric-caption"><fmt:message key="dashboard.activeQueues" bundle="${msg}" /></span>
            <strong>${activeQueues}</strong>
        </div>
        <div class="dashboard-metric metric-green">
            <span class="metric-caption"><fmt:message key="dashboard.waitingPatients" bundle="${msg}" /></span>
            <strong>${waitingCount}</strong>
        </div>
        <div class="dashboard-metric metric-amber">
            <span class="metric-caption"><fmt:message key="dashboard.averageWait" bundle="${msg}" /></span>
            <strong>${avgWaitMinutes} <small><fmt:message key="dashboard.minutesShort" bundle="${msg}" /></small></strong>
        </div>
        <div class="dashboard-metric metric-pink">
            <span class="metric-caption"><fmt:message key="dashboard.servedToday" bundle="${msg}" /></span>
            <strong>${servedToday}</strong>
        </div>
    </section>

    <c:choose>
        <c:when test="${param.result eq 'paused'}">
            <p class="dashboard-flash" role="status"><fmt:message key="dashboard.pausedMessage" bundle="${msg}" /></p>
        </c:when>
        <c:when test="${param.result eq 'resumed'}">
            <p class="dashboard-flash" role="status"><fmt:message key="dashboard.resumedMessage" bundle="${msg}" /></p>
        </c:when>
        <c:when test="${param.result eq 'called'}">
            <p class="dashboard-flash" role="status"><fmt:message key="dashboard.calledMessage" bundle="${msg}" /></p>
        </c:when>
        <c:when test="${param.result eq 'noNext'}">
            <p class="dashboard-flash" role="status"><fmt:message key="dashboard.noNextMessage" bundle="${msg}" /></p>
        </c:when>
    </c:choose>

    <c:if test="${param.result eq 'demoLoaded'}">
        <p class="dashboard-flash" role="status">Sample patients have been added to the selected queue.</p>
    </c:if>
    <c:if test="${param.result eq 'demoSkipped'}">
        <p class="dashboard-flash" role="status">This queue already has patients, or it is paused. Use the existing queue.</p>
    </c:if>

    <c:if test="${param.result eq 'completed'}">
        <p class="dashboard-flash" role="status">Service completed. Ready to call the next patient.</p>
    </c:if>

    <section class="dashboard-content-grid">
        <div class="dashboard-panel live-panel">
            <div class="panel-title-wrap">
                <h2><fmt:message key="dashboard.liveMonitor" bundle="${msg}" /></h2>
                <div class="dashboard-panel-tools">
                    <form class="dashboard-mini-select" method="get" action="${pageContext.request.contextPath}/dashboard">
                        <label for="staffQueue">Department</label>
                        <select id="staffQueue" name="queueId" onchange="this.form.submit()">
                            <c:forEach var="option" items="${queues}">
                                <option value="${option.queueId}" ${option.queueId eq selectedQueueId ? 'selected' : ''}><c:out value="${option.name}"/></option>
                            </c:forEach>
                        </select>
                        <noscript><button type="submit">View</button></noscript>
                    </form>
                    <form action="${pageContext.request.contextPath}/demo/load" method="post" class="dashboard-demo-form">
                        <input type="hidden" name="queueId" value="${queue.queueId}">
                        <input type="hidden" name="_csrf" value="${csrfToken}">
                        <button type="submit" title="Add four sample tickets for a class demonstration">Load demo patients</button>
                    </form>
                </div>
                <c:if test="${not queue.open}">
                    <span class="queue-paused-pill"><fmt:message key="dashboard.paused" bundle="${msg}" /></span>
                </c:if>
            </div>
            <div class="dashboard-table-scroll">
                <table class="dashboard-table">
                    <thead>
                    <tr>
                        <th scope="col"><fmt:message key="dashboard.position" bundle="${msg}" /></th>
                        <th scope="col"><fmt:message key="dashboard.name" bundle="${msg}" /></th>
                        <th scope="col"><fmt:message key="dashboard.status" bundle="${msg}" /></th>
                        <th scope="col"><fmt:message key="dashboard.wait" bundle="${msg}" /></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${not empty servingTicket}">
                        <tr>
                            <td>${servingTicket.position}</td>
                            <td><c:out value="${servingTicket.userName}" /></td>
                            <td><span class="ticket-pill pill-serving"><fmt:message key="dashboard.serving" bundle="${msg}" /></span></td>
                            <td>&ndash;</td>
                        </tr>
                    </c:if>
                    <c:forEach var="ticket" items="${waitingTickets}">
                        <tr>
                            <td>${ticket.position}</td>
                            <td><c:out value="${ticket.userName}" /></td>
                            <td>
                                <c:choose>
                                    <c:when test="${ticket.status eq 'NOTIFIED'}">
                                        <span class="ticket-pill pill-notified"><fmt:message key="dashboard.notified" bundle="${msg}" /></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="ticket-pill pill-waiting"><fmt:message key="dashboard.waiting" bundle="${msg}" /></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${elapsedWaitMinutes[ticket.ticketId]} <fmt:message key="dashboard.minutesShort" bundle="${msg}" /></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty waitingTickets and empty servingTicket}">
                        <tr>
                            <td class="dashboard-no-tickets" colspan="4"><fmt:message key="dashboard.empty" bundle="${msg}" /></td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
            <div class="dashboard-action-area">
                <div class="dashboard-buttons">
                    <form action="${pageContext.request.contextPath}/callnext" method="post">
                        <input type="hidden" name="queueId" value="${queue.queueId}">
                        <input type="hidden" name="_csrf" value="${csrfToken}">
                        <button class="dashboard-primary-btn" type="submit"
                            ${(not queue.open or (empty waitingTickets and empty servingTicket)) ? 'disabled' : ''}>
                            <fmt:message key="dashboard.callNextLong" bundle="${msg}" />
                        </button>
                    </form>
                    <form action="${pageContext.request.contextPath}/togglequeue" method="post">
                        <input type="hidden" name="queueId" value="${queue.queueId}">
                        <input type="hidden" name="_csrf" value="${csrfToken}">
                        <c:choose>
                            <c:when test="${queue.open}">
                                <input type="hidden" name="action" value="pause">
                                <button class="dashboard-outline-btn" type="submit"><fmt:message key="dashboard.pauseQueue" bundle="${msg}" /></button>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="action" value="resume">
                                <button class="dashboard-outline-btn" type="submit"><fmt:message key="dashboard.resumeQueue" bundle="${msg}" /></button>
                            </c:otherwise>
                        </c:choose>
                    </form>
                    <form action="${pageContext.request.contextPath}/complete" method="post">
                        <input type="hidden" name="queueId" value="${queue.queueId}">
                        <input type="hidden" name="_csrf" value="${csrfToken}">
                        <button class="dashboard-outline-btn" type="submit" ${empty servingTicket ? 'disabled' : ''}>Complete service</button>
                    </form>
                </div>
                <p class="dashboard-data-note"><fmt:message key="dashboard.demoDataNote" bundle="${msg}" /></p>
            </div>
        </div>

        <aside class="dashboard-panel analytics-panel">
            <h2><fmt:message key="dashboard.analytics" bundle="${msg}" /></h2>
            <p class="analytics-subtitle"><fmt:message key="dashboard.chartSubtitle" bundle="${msg}" /></p>
            <div class="dashboard-chart" role="img" aria-label="Peak number of patients waiting for each of the last seven days">
                <div class="chart-columns">
                    <c:forEach var="day" items="${weekBars}" varStatus="state">
                        <div class="chart-column">
                            <div class="chart-bar ${state.index eq 2 or state.index eq 4 ? 'bar-strong' : 'bar-light'}"
                                 style="height: ${day.height}%"
                                 title="${day.fullLabel}: ${day.count}"></div>
                        </div>
                    </c:forEach>
                </div>
                <div class="chart-axis-labels">
                    <c:forEach var="day" items="${weekBars}">
                        <span title="${day.fullLabel}">${day.label}</span>
                    </c:forEach>
                </div>
            </div>

            <div class="analytics-facts">
                <p class="analytics-fact-label"><fmt:message key="dashboard.peakUsage" bundle="${msg}" /></p>
                <p class="analytics-fact-value">
                    <c:choose>
                        <c:when test="${empty peakPeriod}"><fmt:message key="dashboard.noAnalytics" bundle="${msg}" /></c:when>
                        <c:otherwise><c:out value="${peakPeriod}" /></c:otherwise>
                    </c:choose>
                </p>
                <p class="analytics-fact-label"><fmt:message key="dashboard.monthVolume" bundle="${msg}" /></p>
                <p class="analytics-fact-value">${servedThisMonth} <fmt:message key="dashboard.ticketsServed" bundle="${msg}" /></p>
            </div>
        </aside>
    </section>
</main>
</body>
</html>
