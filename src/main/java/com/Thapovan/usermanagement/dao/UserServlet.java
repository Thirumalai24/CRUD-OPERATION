package com.Thapovan.usermanagement.dao;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;



@WebServlet("/")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = LogManager.getLogger(UserServlet.class);

    private UserDAO userDAO;

    
    public void init() {
        userDAO = UserDAO.getInstance();   
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getServletPath();

        try {
            switch (action) {
                case "/new":
                    showNewForm(request, response);
                    break;

                case "/insert":
                    insertUser(request, response);
                    break;

                case "/edit":
                    showEditForm(request, response);
                    break;

                case "/update":
                    updateUser(request, response);
                    break;

                case "/delete":
                    deleteUser(request, response);
                    break;

                default:
                    listUser(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- delete user details
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        userDAO.deleteUser(id);
        response.sendRedirect("list");
    }

    // --- list all user details---
    private void listUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        List<User> listUser = userDAO.selectAllUsers();
        request.setAttribute("listUser", listUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user-list.jsp");
        dispatcher.forward(request, response);
    }
    //update user
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, SQLException {
        // Extract parameters from the request
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String country = request.getParameter("country");
        String mobile = request.getParameter("mobile");
        String gender = request.getParameter("gender");

        // Validate email
        if (!userDAO.isEmailValid(email)) {
            request.setAttribute("error", "Please enter a valid email address. Sample@gmail.com");
            request.setAttribute("user", new User(id, name, email, country, mobile, gender));
            showEditForm(request, response);
            return;
        }

        // Validate name
        if (!userDAO.isValidateName(name)) {
            request.setAttribute("error", "Invalid name. Names should only contain letters and spaces.");
            request.setAttribute("user", new User(id, name, email, country, mobile, gender));
            showEditForm(request, response);
            return;
        }

        // Create a User object with updated details
        User user = new User(id, name, email, country, mobile, gender);

        // Attempt to update the user in the database
        boolean updateSuccess = userDAO.updateUser(user);

        if (updateSuccess) {
            // Redirect to the user list page if the update is successful
            response.sendRedirect("list");
        } else {
            // Handle the case where the update failed (e.g., user does not exist)
            response.sendRedirect("error.jsp"); // Redirect to an error page
        }
    }



    // ---form edit----
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
    	
        int id = Integer.parseInt(request.getParameter("id"));
        User existingUser = userDAO.selectUser(id);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("user-form.jsp");
        request.setAttribute("user", existingUser); 
        
        // setAttribute() is called by request obj.
        // setAttribute() it takes input as an obj, sends the data from servlet to requesting obj( string name, object obj)
        dispatcher.forward(request, response);
    }

    // ---Add new form---
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("user-form.jsp");
        dispatcher.forward(request, response);
    }

    // ---- Insert New user----
    private void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String country = request.getParameter("country");
        String mobile = request.getParameter("mobile");
        String gender = request.getParameter("gender");

        // Validate email
        if (!userDAO.isEmailValid(email)) {
            request.setAttribute("error", "Please enter a valid email address. Sample@gmail.com");
            showNewForm(request, response);
            return;
        }
        
        // Validate name
        if (!userDAO.isValidateName(name)) {
            request.setAttribute("error", "Invalid name. Names should only contain letters and spaces.");
            showNewForm(request, response);
            return;
        }

        // Check if email already exists in the database
        boolean emailExists = userDAO.checkEmailExists(email);

        if (emailExists) {
            request.setAttribute("error", "The entered email already exists.");
            showNewForm(request, response);
            return;
        }

        // If all validations pass, proceed with insertion
        User newUser = new User(0, name, email, country, mobile, gender);
        userDAO.insertUser(newUser);
        response.sendRedirect("list");
    }
   
    @Override
    public void destroy() {
        logger.info("Servlet is destroyed");
    }

}