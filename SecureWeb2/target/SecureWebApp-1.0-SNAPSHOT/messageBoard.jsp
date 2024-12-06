<%@ page import="java.sql.ResultSet" %>
<!DOCTYPE html>
<html>
<head>
    <title>Message Board</title>
</head>
<body>
<h1>Message Board</h1>
<form method="get">
    <label>Search:</label>
    <input type="text" name="keyword">
    <button type="submit">Search</button>
</form>
<ul>
    <%
        ResultSet messages = (ResultSet) request.getAttribute("messages");
        while (messages != null && messages.next()) {
            String username = messages.getString("username");
            String message = messages.getString("message");
            String timestamp = messages.getString("timestamp");
    %>
    <li><strong><%= username %></strong>: <%= message %> (<em><%= timestamp %></em>)</li>
    <%
        }
    %>
</ul>
<form method="post">
    <label>Message:</label><br>
    <textarea name="message"></textarea><br>
    <button type="submit">Post</button>
</form>
</body>
</html>
