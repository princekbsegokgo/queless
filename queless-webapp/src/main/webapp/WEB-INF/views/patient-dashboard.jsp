<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:if test="${empty sessionScope.lang}"><c:set var="lang" value="en" scope="session"/></c:if>
<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages" var="msg"/>
<fmt:message key="join.namePlaceholder" bundle="${msg}" var="namePlaceholder"/>
<c:set var="openQueueCount" value="0"/>
<c:set var="totalWaiting" value="0"/>
<c:forEach var="q" items="${queues}">
  <c:if test="${q.open}"><c:set var="openQueueCount" value="${openQueueCount + 1}"/></c:if>
  <c:set var="totalWaiting" value="${totalWaiting + waitingByQueue[q.queueId]}"/>
</c:forEach>
<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title><fmt:message key="patient.dashboardTitle" bundle="${msg}"/></title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body class="business-dashboard-page patient-dashboard-page">
<main class="biz-dashboard">
  <header class="dashboard-topbar">
    <h1><fmt:message key="patient.dashboardTitle" bundle="${msg}"/></h1>
    <div class="dashboard-header-actions">
<nav class="dashboard-nav" aria-label="Dashboard navigation">
                <a class="dashboard-nav-link" href="${pageContext.request.contextPath}/"><fmt:message key="nav.home" bundle="${msg}"/></a>
                <a class="dashboard-nav-link ${'is-active'}" href="${pageContext.request.contextPath}/joinqueue" aria-current="page"><fmt:message key="nav.customerDashboard" bundle="${msg}"/></a>
                <a class="dashboard-nav-link ${''}" href="${pageContext.request.contextPath}/dashboard" ><fmt:message key="nav.staffDashboard" bundle="${msg}"/></a>
            </nav>
      <div class="dashboard-language" aria-label="Language">
        <a href="${pageContext.request.contextPath}/language?lang=en&amp;returnTo=/joinqueue" class="${sessionScope.lang eq 'en' ? 'selected' : ''}" lang="en">EN</a>
        <span aria-hidden="true">/</span>
        <a href="${pageContext.request.contextPath}/language?lang=tn&amp;returnTo=/joinqueue" class="${sessionScope.lang eq 'tn' ? 'selected' : ''}" lang="tn">TSW</a>
      </div>
      <span class="dashboard-clinic"><c:out value="${business.name}"/></span>
    </div>
  </header>
  <section class="dashboard-metrics" aria-label="Patient queue summary">
    <div class="dashboard-metric metric-blue"><span class="metric-caption"><fmt:message key="patient.departments" bundle="${msg}"/></span><strong>${queues.size()}</strong></div>
    <div class="dashboard-metric metric-green"><span class="metric-caption"><fmt:message key="patient.openDepartments" bundle="${msg}"/></span><strong>${openQueueCount}</strong></div>
    <div class="dashboard-metric metric-amber"><span class="metric-caption"><fmt:message key="dashboard.waitingPatients" bundle="${msg}"/></span><strong>${totalWaiting}</strong></div>
    <div class="dashboard-metric metric-pink">
      <span class="metric-caption"><fmt:message key="nav.myTicket" bundle="${msg}"/></span>
      <c:choose>
        <c:when test="${not empty sessionScope.myTicketToken}"><a class="patient-metric-link" href="${pageContext.request.contextPath}/status?ticket=${sessionScope.myTicketToken}"><fmt:message key="patient.track" bundle="${msg}"/></a></c:when>
        <c:otherwise><strong class="patient-no-ticket">&mdash;</strong></c:otherwise>
      </c:choose>
    </div>
  </section>
  <c:if test="${param.paused eq '1'}"><p class="dashboard-flash" role="alert"><fmt:message key="join.pausedNotice" bundle="${msg}"/></p></c:if>
  <c:if test="${param.invalid eq '1'}"><p class="dashboard-flash patient-error" role="alert"><fmt:message key="join.invalidName" bundle="${msg}"/></p></c:if>
  <c:if test="${param.notfound eq '1'}"><p class="dashboard-flash" role="status"><fmt:message key="join.notFound" bundle="${msg}"/></p></c:if>
  <section class="dashboard-content-grid" aria-label="Join a patient queue">
    <div class="dashboard-panel live-panel">
      <div class="panel-title-wrap"><h2><fmt:message key="patient.availableServices" bundle="${msg}"/></h2></div>
      <p class="patient-panel-intro"><fmt:message key="patient.servicesHelp" bundle="${msg}"/></p>
      <div class="dashboard-table-scroll">
        <table class="dashboard-table">
          <thead><tr><th scope="col"><fmt:message key="join.department" bundle="${msg}"/></th><th scope="col"><fmt:message key="dashboard.waiting" bundle="${msg}"/></th><th scope="col"><fmt:message key="dashboard.status" bundle="${msg}"/></th></tr></thead>
          <tbody>
            <c:forEach var="queue" items="${queues}">
              <tr><td><c:out value="${queue.name}"/></td><td>${waitingByQueue[queue.queueId]}</td>
                <td><c:choose>
                  <c:when test="${queue.open}"><span class="ticket-pill pill-serving"><fmt:message key="join.open" bundle="${msg}"/></span></c:when>
                  <c:otherwise><span class="ticket-pill pill-notified"><fmt:message key="join.queuePaused" bundle="${msg}"/></span></c:otherwise>
                </c:choose></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      <div class="dashboard-action-area"><p class="dashboard-data-note"><fmt:message key="join.disclaimer" bundle="${msg}"/></p></div>
    </div>
    <aside class="dashboard-panel analytics-panel patient-join-panel">
      <h2><fmt:message key="join.portalTitle" bundle="${msg}"/></h2>
      <p class="analytics-subtitle"><fmt:message key="join.portalDescription" bundle="${msg}"/></p>
      <form class="patient-dashboard-form" action="${pageContext.request.contextPath}/joinqueue" method="post">
        <input type="hidden" name="_csrf" value="${csrfToken}">
        <label for="patient-department"><fmt:message key="join.department" bundle="${msg}"/></label>
        <select id="patient-department" name="queueId" required ${openQueueCount eq 0 ? 'disabled' : ''}>
          <option value="" selected disabled><fmt:message key="patient.chooseDepartment" bundle="${msg}"/></option>
          <c:forEach var="queue" items="${queues}"><c:if test="${queue.open}"><option value="${queue.queueId}"><c:out value="${queue.name}"/></option></c:if></c:forEach>
        </select>
        <label for="patient-name"><fmt:message key="join.yourName" bundle="${msg}"/></label>
        <input id="patient-name" type="text" name="userName" maxlength="80" autocomplete="name" placeholder="${namePlaceholder}" required ${openQueueCount eq 0 ? 'disabled' : ''}>
        <button class="dashboard-primary-btn" type="submit" ${openQueueCount eq 0 ? 'disabled' : ''}><fmt:message key="join.getTicket" bundle="${msg}"/></button>
      </form>
      <c:if test="${openQueueCount eq 0}"><p class="patient-paused-note" role="status"><fmt:message key="join.pausedDetails" bundle="${msg}"/></p></c:if>
      <c:if test="${not empty sessionScope.myTicketToken}"><a class="patient-track-ticket" href="${pageContext.request.contextPath}/status?ticket=${sessionScope.myTicketToken}"><fmt:message key="home.trackTicket" bundle="${msg}"/></a></c:if>
      <p class="patient-help-note"><fmt:message key="home.demoNotice" bundle="${msg}"/></p>
    </aside>
  </section>
</main>
</body>
</html>
