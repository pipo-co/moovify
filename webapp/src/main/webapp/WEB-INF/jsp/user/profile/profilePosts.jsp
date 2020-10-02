<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="user.profile.Profile" arguments="${loggedUser.username}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body style="min-height: 1000px">
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div>
    <c:set var="currentState" value="0" scope="request" />
    <jsp:include page="profile.jsp"/>

    <div class="uk-container">
        <c:if test="${empty posts}">
            <h2 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="search.posts.postsNotFound"/> </h2>
        </c:if>
        <sec:authorize access="hasRole('USER')">
            <c:set var="posts" value="${posts}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
        </sec:authorize>
    </div>

</body>
</html>