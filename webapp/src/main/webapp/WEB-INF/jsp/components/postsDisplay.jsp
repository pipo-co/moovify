<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="customTag" uri="http://www.paw.itba.edu.ar/moovify/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:useBean id="posts" scope="request" type="ar.edu.itba.paw.models.PaginatedCollection<ar.edu.itba.paw.models.Post>"/>

<sec:authorize access="isAuthenticated()">
    <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
</sec:authorize>

<div class="uk-flex uk-flex-wrap">
    <c:forEach items="${posts.results}" var="post">
        <div class="uk-width-1-1">
            <div class="uk-flex">
                <div class="uk-width-expand uk-margin-small-top uk-margin-small-bottom uk-text-truncate">
                    <p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                        <c:choose>
                            <c:when test="${post.user.enabled}">
                                <c:set var="name" value="${post.user.username}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="name"><spring:message code="user.notEnabled.name"/></c:set>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${post.category.name eq 'watchlist'}">
                                <span uk-icon="icon: list; ratio: 0.8"></span>
                            </c:when>
                            <c:when test="${post.category.name eq 'critique'}">
                                <span uk-icon="icon: check; ratio: 0.8"></span>
                            </c:when>
                            <c:when test="${post.category.name eq 'news'}">
                                <span uk-icon="icon: warning; ratio: 0.8"></span>
                            </c:when>
                            <c:when test="${post.category.name eq 'debate'}">
                                <span uk-icon="icon: users; ratio: 0.8"></span>
                            </c:when>
                        </c:choose>
                        <spring:message code="${post.category.name}" var="category"/>
                        <spring:message code="postDisplay.meta.description" arguments="${category}, ${name}"/>
                        <c:if test="${post.user.admin && post.user.enabled}">
                            <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
                        </c:if>
                        <spring:message code="postDisplay.meta.votes" arguments="${post.totalLikes}"/>
                        <span uk-icon="icon: <c:out value="${post.totalLikes >= 0 ? 'chevron-up':'chevron-down'}"/>; ratio: 0.8"></span>
                    </p>

                    <a href="<c:url value="/post/${post.id}"/>">
                        <sec:authorize access="isAuthenticated()">
                            <c:set var="bookmarked" value="${customTag:hasUserBookmarkedPost(loggedUser, post)}"/>
                            <c:if test="${bookmarked}">
                                <span class="iconify small-iconify" data-icon="mdi-bookmark-check" data-inline="false"></span>
                                <c:out value="-"/>
                            </c:if>
                        </sec:authorize>
                        <c:out value="${post.title}"/>
                    </a>


                    <p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                        <spring:message code="postDisplay.meta.moviesDiscussed"/>
                        <c:forEach items="${post.movies}" var="movie">
                            <a
                                    href="<c:url value="/movie/${movie.id}"/>">
                                <spring:message code="postDisplay.meta.list" arguments="${movie.title}"/>
                            </a>
                        </c:forEach>
                    </p>
                </div>
                <div class="uk-width-auto">
                    <p class="uk-text-meta uk-text-right uk-margin-small-top uk-margin-remove-bottom uk-padding-small">
                        <c:if test="${post.daysSinceCreation > 0}">
                            <spring:message code="postDisplay.meta.age.days" arguments="${post.daysSinceCreation}"/>
                        </c:if>
                        <c:if test="${post.daysSinceCreation == 0 && post.hoursSinceCreation > 0}">
                            <spring:message code="postDisplay.meta.age.hours" arguments="${post.hoursSinceCreation}"/>
                        </c:if>
                        <c:if test="${post.daysSinceCreation == 0 && post.hoursSinceCreation == 0}">
                            <spring:message code="postDisplay.meta.age.minutes" arguments="${post.minutesSinceCreation}"/>
                        </c:if>
                    </p>
                </div>
            </div>
        </div>
    </c:forEach>
</div>