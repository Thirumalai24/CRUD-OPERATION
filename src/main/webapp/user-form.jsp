<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="com.Thapovan.usermanagement.dao.UserDAO" %>
<%@ page import="com.Thapovan.usermanagement.dao.User" %>



<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>User Management App</title>
   
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

        .navbar {
            position: relative;
            display: -ms-flexbox;
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            justify-content: space-between;
            padding: 0.5rem 1rem;
        }

        .container {
            max-width: 500px;
            margin: 20px auto;
            background-color: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 15px rgba(0, 0, 0, 0.2);
        }

        h2 {
            color: #007bff;
            text-align: center;
            margin-bottom: 30px;
        }

        form {
            display: flex;
            flex-direction: column;
        }

        label {
            font-weight: bold;
            margin-bottom: 8px;
            color: #495057;
            display: inline-block;
        }

        .form-check-label {
            margin-bottom: 0;
        }

        .form-check-inline {
            display: inline-flex;
            align-items: center;
            padding-left: 0;
            margin-right: 0.75rem;
        }

        .form-group {
            margin-bottom: 20px;
        }

        input, select {
            width: 100%;
            padding: 12px;
            box-sizing: border-box;
            border: 1px solid #ced4da;
            border-radius: 5px;
            font-size: 16px;
        }

        .error-message {
            color: #dc3545;
            font-size: 14px;
            margin-top: 4px;
        }

        .alert-danger {
            color: #dc3545;
        }

        .alert {
            position: relative;
            margin-bottom: 1rem;
            border: 1px solid transparent;
        }

        button {
            background-color: #28a745;
            color: #fff;
            border: none;
            border-radius: 5px;
            padding: 12px;
            cursor: pointer;
            transition: background-color 0.3s ease;
            width: 100%;
            text-align: center;
            margin-top: 20px;
            font-size: 16px;
        }

        button:hover {
            background-color: #218838;
        }
    </style>
<script type="text/javascript">
   
    function validateEmail() {
        var emailInput = document.getElementById("email");
        var emailErrorDiv = document.querySelector(".alert-danger");
        var emailExistsInput = document.getElementsByName("emailExists")[0];

        // Get the current value entered in the email input field
        var emailValue = emailInput.value;
        var emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z]+\.[a-zA-Z]{2,3}$/;

        // Check if the entered email does not match the email format
        if (!emailRegex.test(emailValue)) {
            emailErrorDiv.innerHTML = "Please enter a valid email address. Example: Sample@gmail.com";
            return false;
        } else {
            emailErrorDiv.innerHTML = "";
        }

        // Check if the emailExistsInput value is "true" (the entered email already exists)
        if (emailExistsInput.value === "true") {
            emailErrorDiv.innerHTML = "The entered email already exists.";
            emailInput.focus();
            return false;
        }

        // If all checks pass, return true to allow form submission
        return true;
    }
</script>

    
</head>

<body>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

    <%@ page import="java.sql.*" %>

    <header>
        <nav class="navbar" style="background-color: #007bff" >
            <div class="navbar-brand">USER MANAGEMENT APP</div>
            <div class="navbar-nav">
                <a href="<%=request.getContextPath()%>/list" style="color: #fff;">USERS</a>
            </div>
        </nav>
    </header>
    <br>

    <div class="container" >

        <c:choose>
            <c:when test="${user != null}">
                <form action="update" method="post" onsubmit=" return validateEmail();">
            </c:when>
            <c:otherwise>
                <form action="insert" method="post" onsubmit="return validateEmail();">
            </c:otherwise>
        </c:choose>

        <c:if test="${user != null}">
            <input type="hidden" name="id" value="<c:out value='${user.id}' />" />
        </c:if>

        <h2>NEW USER</h2>

        <div class="form-group">
            <label for="name">Name</label>
            <input type="text" value="<c:out value='${user.name}'/>" class="form-control" 
                name="name" placeholder="Enter your Name" 
                id="name" required="required" autocomplete="name"> 

            <!-- Display name error message -->
            <c:if test="${not empty requestScope.nameError}">
                <div class="error-message">
                    ${requestScope.nameError}
                </div>
            </c:if>
        </div>

        <div class="form-group">
            <label for="email">Email</label>
            <input type="text" value="<c:out value='${user.email}'/>" class="form-control" 
                name="email" pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z]+\.[a-zA-Z]{2,3}$" 
                id="email" placeholder="Enter Your Email" required="required" autocomplete="email">

            <!-- Hidden input for email existence flag -->
            <input type="hidden" name="emailExists" value="${requestScope.emailExists}">

            <!-- Display error messages if they exist -->
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">
                    ${requestScope.error}
                </div>
            </c:if>
        </div>

        <div class="form-group">
            <label for="mobile">Mobile</label>
            <input type="text" value="<c:out value='${user.mobile}'/>" class="form-control" 
                name="mobile" pattern="[0-9]{10}" placeholder="Enter your Mobile" 
                id="mobile" required="required" autocomplete="tel">
        </div>

        <div class="form-group">
            <label>Gender:</label>
            <div class="form-check form-check-inline">
                <input type="radio" id="male" name="gender" value="Male" checked class="form-check-input">
                <label for="male" class="form-check-label">Male</label>
            </div>
            <div class="form-check form-check-inline">
                <input type="radio" id="female" name="gender" value="Female" class="form-check-input">
                <label for="female" class="form-check-label">Female</label>
            </div>
        </div>

        <div class="form-group">
            <label for="country">Country:</label>
            <select name="country" id="country" class="form-control" required="required">
                <option value="" >Select Country</option>
                <%
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        String jdbcURL = "jdbc:mysql://localhost:3306/demo";
                        String jdbcUsername = "root";
                        String jdbcPassword = "Dell7470";

                        try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
                            Statement stmt = connection.createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT country_name from country")) {

                            while (rs.next()) {
                                String name = rs.getString("country_name");
                %>
                                <option  ><%=name%></option>
                <%
                            }
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                %>
            </select>
        </div>

        <button type="submit" class="btn btn-success">Save</button>
    </form>

    </div>
   


</body>
</html>
