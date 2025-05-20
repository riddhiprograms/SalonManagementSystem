<%-- 
    Document   : success
    Created on : 13-Apr-2025, 2:47:08 am
    Author     : RIDDHI PARGHEE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Registration Successful</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script>
        setTimeout(function () {
            window.location.href = "login.jsp";
        }, 3000); // Redirect after 3 seconds
    </script>
</head>
<body>
    <div class="container mt-5">
        <div class="alert alert-success">
            <h4>Registration Successful!</h4>
            <p>Your account has been created successfully. You will be redirected to the login page shortly.</p>
        </div>
    </div>
</body>
</html>
