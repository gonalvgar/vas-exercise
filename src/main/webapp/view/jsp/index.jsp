<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<h1><c:out value="${welcome}"/></h1>
<body>If you've already selected a file, you can go to <a href="http://localhost:8080/metrics/view">METRICS</a>  or <a href="http://localhost:8080/kpis/view">KPIS</a> to receive some information about it! </body>
</html>