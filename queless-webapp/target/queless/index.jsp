<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/includes/header.jspf" %>

<section class="hero">
    <h1><fmt:message key="home.title" bundle="${msg}" /></h1>
    <p><fmt:message key="home.subtitle" bundle="${msg}" /></p>

    <div class="button-row">
        <a class="button primary" href="${pageContext.request.contextPath}/joinqueue">
            <fmt:message key="nav.join" bundle="${msg}" />
        </a>
        <a class="button secondary" href="${pageContext.request.contextPath}/dashboard">
            <fmt:message key="nav.dashboard" bundle="${msg}" />
        </a>
    </div>
</section>

<%@ include file="/WEB-INF/includes/footer.jspf" %>
