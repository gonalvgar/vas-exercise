<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
<td>Total number of rows in the db: <c:out value="${rows}"/></td><hr>
<td>Total number of calls in the db: <c:out value="${calls}"/></td><hr>
<td>Total number of messages in the db:<c:out value="${msg}"/></td><hr>
<td>Total number of ccs by origin:<c:out value="${origin}"/></td><hr>
<td>Total number of ccs by destination:<c:out value="${destination}"/></td><hr>
<td>Total number of files processed by the system: <c:out value="${files}"/></td><hr>
<td><c:forEach items="${time}" var="item" varStatus="loop">
    <tr>
        <td>Time to process json file <c:out value="${loop.index +1}" /> is <c:out value="${item}"/> ms</td><br/>
    </tr>
</c:forEach>
</td>
<a href="http://localhost:8080">Return to main page</a>
</body>
</html>