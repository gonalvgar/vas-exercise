<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
<tr>
    <td>Values wrong are: <c:out value="${wrong}"/></td><hr>
    <td>Values empty are: <c:out value="${emptyRows}"/> </td> <hr>
    <td>Values missing are: <c:out value="${missing}"/></td> <hr>
    <td>List of words used ordered by concurrence: <br/> <c:forEach items="${wordsOrder}" var="entry">
       <li> The word ${entry.key}, used  ${entry.value} times</li>
    </c:forEach>
    </td><hr>
    <td>List of calls grouped by cc:<br/>  <c:forEach items="${cc}" var="entry">
       <li> The cc ${entry.key}, has a total of  ${entry.value} calls</li>
    </c:forEach>
    </td><hr>
    <td>Average duration of calls by cc:<br/>  <c:forEach items="${duration}" var="entry">
        <li> The cc ${entry.key}, has an average duration of  ${entry.value} s </li>
    </c:forEach>
    </td><hr>
    <td>Relation between KO and OK calls:<br/>  <c:forEach items="${relation}" var="entry">
        There's a ${entry.value} %, for the status code: " ${entry.key} "<br/>
    </c:forEach>
    </td>
</tr>
<a href="http://localhost:8080">Return to main page</a>
</body>
</html>