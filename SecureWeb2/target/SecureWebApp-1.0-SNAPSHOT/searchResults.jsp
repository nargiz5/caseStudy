<%@ page session="true" %>
<%
  String username = (String) session.getAttribute("user");
  if (username == null) {
    response.sendRedirect("login.jsp");
  }
%>
<!DOCTYPE html>
<html>
<head>
  <title>Search Results</title>
</head>
<body>
<h2>Search Results</h2>
<%-- Dynamically populated results --%>
<div>
  <%
    // Placeholder for messages fetched from the database
  %>
</div>
<a href="messageBoard.jsp">Back to Message Board</a>
</body>
</html>
