<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>User Management App</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        header {
            background-color: #007bff;
            color: #fff;
            padding: 15px 0;
            text-align: center;
        }

        .container {
             max-width: 900px;
            margin: 20px auto;
            background-color: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 15px rgba(0, 0, 0, 0.2);
        }

        h2 {
           color: #007bff;;
            text-align: center;
            margin-bottom: 30px;
        }
        h3.text-center{
        color: #007bff;
        }

        .form-group {
            margin-bottom: 20px;
        }

        header a {
            color: white !important;
            font-size: 20px;
        }

        th {
            background-color: #343a40;
            color: white;
        }

        
    </style>

    <script>
        function confirmDelete() {
            return confirm("Are you sure you want to delete this user?");
        }
    </script>

</head>

<body>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


    <header>
        <nav class="navbar">
            <div class="navbar-brand">USER MANAGEMENT APP</div>
            <ul class="navbar-nav">
                <li><a href="<%=request.getContextPath()%>/list">USERS</a></li>
            </ul>
        </nav>
    </header>
    <br>

    <div class="container">
        <h3 class="text-center">LIST OF USERS</h3>
        <hr>
        <div class=" text-center">
            <a href="<%=request.getContextPath()%>/new" class="btn btn-success">Add New User</a>
        </div>
        <br>
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Mobile No</th>
                    <th>Gender</th>
                    <th>Country</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="user" items="${listUser}">
                    <tr>
                        <td><c:out value="${user.id}" /></td>
                        <td><c:out value="${user.name}" /></td>
                        <td><c:out value="${user.email}" /></td>
                        <td><c:out value="${user.mobile}" /></td>
                        <td><c:out value="${user.gender}" /></td>
                        <td><c:out value="${user.country}" /></td>
                        <td>
                            <a href="<%=request.getContextPath()%>/edit?id=<c:out value='${user.id}' />">Edit</a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="<%=request.getContextPath()%>/delete?id=<c:out value='${user.id}' />"
                                onclick="return confirmDelete()">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

</body>

</html>
