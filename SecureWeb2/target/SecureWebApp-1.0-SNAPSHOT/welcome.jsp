<!DOCTYPE html>
<html>
<head>
  <title>Welcome</title>
</head>
<body>
<h1>Welcome, <%= request.getAttribute("username") %>!</h1>
<img src="profile_pics/default.jpg" alt="Profile Picture"><br>
<form action="upload" method="post" enctype="multipart/form-data">
  <input type="file" name="profile_picture">
  <button type="submit">Upload</button>
</form>
<a href="messageboard">View Messages</a>
</body>
</html>
