<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Strategy Pattern Example</title>
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/main.css" />" />
</head>

    <nav>
        <ul class="top">
            <c:forEach items="${navigation}" var="item">
                <li><a href="${item.value}">${item.key}</a></li>
            </c:forEach>
        </ul>
    </nav>

    <main>
        <p>
            ${content}
        </p>
    </main>

    <nav>
        <ul class="bottom">Switch User:
            <c:forEach items="${links}" var="item">
                <c:choose>
                    <c:when test="${not empty item.value}">
                        <li><a href="${item.value}">${item.key}</a></li>
                    </c:when>
                    <c:otherwise>
                        <li>${item.key}</li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </ul>
    </nav>

</html>