<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Strategy Pattern Example</title>
</head>

    <ul>
        <c:forEach items="${navigation}" var="item">
            <li><a href="${item.value}">${item.key}</a></li>
        </c:forEach>
    </ul>

    <p>
        Lorem ipsum...
    </p>

    <p>Switch User:
        <c:forEach items="${links}" var="item">
            <c:choose>
                <c:when test="${not empty item.value}">
                    <a href="${item.value}">${item.key}</a>
                </c:when>
                <c:otherwise>
                    ${item.key}
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </p>

</html>