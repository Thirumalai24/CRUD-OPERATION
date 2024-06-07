package com.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Thapovan.usermanagement.dao.User;
import com.Thapovan.usermanagement.dao.UserDAO;
import com.Thapovan.usermanagement.dao.UserServlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegisterationTest {

    @Mock
    private UserDAO userDAO;	// Mock UserDAO object

    @Mock
    private HttpServletRequest request;		// Mock HttpServletRequest object

    @Mock
    private HttpServletResponse response;	// Mock HttpServletResponse object

    @Mock
    private RequestDispatcher dispatcher;	//Mock RequestDispatcher object
    

    @InjectMocks
    private UserServlet userServlet;   	//Used to inject mocked dependencies into the UserServlet object.

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  //called to initialize the mock instances.
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }
    
//Positive - TestCase InsertUser
    @Test
    public void testInsertUser() throws ServletException, IOException, SQLException {
        // Set up request parameters
        when(request.getServletPath()).thenReturn("/insert");
        when(request.getParameter("name")).thenReturn("Thirumalai");
        when(request.getParameter("email")).thenReturn("Thirumalai@gmail.com");
        when(request.getParameter("country")).thenReturn("India");
        when(request.getParameter("mobile")).thenReturn("9892345432");
        when(request.getParameter("gender")).thenReturn("Male");

        // Mock validation methods
        when(userDAO.isEmailValid("Thirumalai@gmail.com")).thenReturn(true);
        when(userDAO.isValidateName("Thirumalai")).thenReturn(true);
        when(userDAO.checkEmailExists("Thirumalai@gmail.com")).thenReturn(false);

        // Call the  method for request and response
        userServlet.doPost(request, response);

        // creating a new ArgumentCaptor object that is capable of capturing User objects   
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        
        verify(userDAO).insertUser(userCaptor.capture());
        User newUser = userCaptor.getValue();
        
        assertEquals("Thirumalai", newUser.getName());
        assertEquals("Thirumalai@gmail.com", newUser.getEmail());
        assertEquals("India", newUser.getCountry());
        assertEquals("9892345432", newUser.getMobile());
        assertEquals("Male", newUser.getGender());

        // Verify that the user was redirected to the list page
        verify(response).sendRedirect("list");
        System.out.println("Positive - TestCase InsertUser() passed!");
    }
    
 // Negative TestCase InsertUserWithInvalidName
    @Test
    public void testInsertUserWithInvalidName() throws ServletException, IOException, SQLException {
        // Set up request parameters
        when(request.getServletPath()).thenReturn("/insert");
        when(request.getParameter("name")).thenReturn("varun123"); // Invalid name
        when(request.getParameter("email")).thenReturn("varun@gmail.com");
        when(request.getParameter("country")).thenReturn("China");
        when(request.getParameter("mobile")).thenReturn("7772345477");
        when(request.getParameter("gender")).thenReturn("Male");

        // Mock validation methods
        when(userDAO.isEmailValid("varun@gmail.com")).thenReturn(true);
        when(userDAO.isValidateName("varun123")).thenReturn(false);  // Invalid name

        userServlet.doPost(request, response);
        verify(request).setAttribute("error", "Invalid name. Names should only contain letters and spaces.");
        verify(request.getRequestDispatcher("user-form.jsp")).forward(request, response);
        
        System.out.println("Negative - TestCase InsertUserWithInvalidName() passed!");
    }

       
// Negative - TestCase InsertUserWithInvalidEmail
    @Test
    public void testInsertUserWithInvalidEmail() throws ServletException, IOException, SQLException {
        // Set up request parameters
        when(request.getServletPath()).thenReturn("/insert");
        when(request.getParameter("name")).thenReturn("Venkat");
        when(request.getParameter("email")).thenReturn("Venkat"); // Invalid email
        when(request.getParameter("country")).thenReturn("USA");
        when(request.getParameter("mobile")).thenReturn("6363345466");
        when(request.getParameter("gender")).thenReturn("Male");

        when(userDAO.isEmailValid("Venkat")).thenReturn(false); //Invalid emai
        userServlet.doPost(request, response);

        // Verify that the user was not inserted
        verify(userDAO, never()).insertUser(any(User.class));

        // Verify that the error message is set in the request
        verify(request).setAttribute("error", "Please enter a valid email address. Sample@gmail.com");
        verify(request.getRequestDispatcher("error.jsp")).forward(request, response);
        System.out.println("Negative - TestCase InsertUserWithInvalidEmail() passed!");
    }


// // Negative - Testcase InsertUserWithEmailAlreadyExists
//    @Test
//    public void testInsertUserWithEmailAlreadyExists() throws ServletException, IOException, SQLException {
//        // Setup request parameters
//        when(request.getServletPath()).thenReturn("/insert");
//        when(request.getParameter("name")).thenReturn("John Doe");
//        when(request.getParameter("email")).thenReturn("johndoe@example.com"); // Existing email
//        when(request.getParameter("country")).thenReturn("USA");
//        when(request.getParameter("mobile")).thenReturn("1234567890");
//        when(request.getParameter("gender")).thenReturn("Male");
//
//        when(userDAO.checkEmailExists("johndoe@example.com")).thenReturn(false);
//
//        userServlet.doPost(request, response);
//
//        // Verify that the user was not inserted
//        verify(userDAO, never()).insertUser(any(User.class));
//
//        // Verify that the error message is set in the request
//        verify(request).setAttribute("error", "The entered email already exists.");
//        verify(request.getRequestDispatcher("user-form.jsp")).forward(request, response);
//        System.out.println("Negative - TestCase InsertUserWithEmailAlreadyExists() passed!");
//    }


//Positive - TestCase UpdateUser
    @Test
    public void testUpdateUser() throws ServletException, IOException, SQLException {
       
        when(request.getServletPath()).thenReturn("/update");
        
        when(request.getParameter("id")).thenReturn("1"); // Assuming the user id is 1
        when(request.getParameter("name")).thenReturn("jaiganesh");
        when(request.getParameter("email")).thenReturn("jaiganesh@gmail.com");
        when(request.getParameter("country")).thenReturn("USA");
        when(request.getParameter("mobile")).thenReturn("6999345432");
        when(request.getParameter("gender")).thenReturn("Male");

        when(userDAO.isEmailValid("jaiganesh@gmail.com")).thenReturn(true);
        when(userDAO.isValidateName("jaiganesh")).thenReturn(true);  
        when(userDAO.checkEmailExists("jaiganesh@gmail.com")).thenReturn(true);

        // Mock the existing user
        User existingUser = new User(1, "Thirumalai", "Thirumalai@gmail.com", "India", "9892345432", "Male");
        when(userDAO.selectUser(1)).thenReturn(existingUser);

        // Mock the update to succeed
        when(userDAO.updateUser(any(User.class))).thenReturn(true);
        userServlet.doPost(request, response);

        // Verify that the user was updated 
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).updateUser(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        
        assertEquals(existingUser.getId(), updatedUser.getId());
        
        assertEquals("jaiganesh", updatedUser.getName());
        assertEquals("jaiganesh@gmail.com", updatedUser.getEmail());
        assertEquals("USA", updatedUser.getCountry());
        assertEquals("6999345432", updatedUser.getMobile());
        assertEquals("Male", updatedUser.getGender());

        // Verify that the user was redirected to the list page
        verify(response).sendRedirect("list");
        System.out.println("Positive - TestCase UpdateUser() passed!");
    }
    
//Negative - Testcase UpdateUserWithInvalidName
    @Test
    public void testUpdateUserWithInvalidName() throws ServletException, IOException, SQLException {

        when(request.getServletPath()).thenReturn("/update");
        when(request.getParameter("id")).thenReturn("1"); // Assuming the user id is 1
        when(request.getParameter("name")).thenReturn("jaiganesh123"); // Invalid name
        when(request.getParameter("email")).thenReturn("jaiganesh@gmail.com");
        when(request.getParameter("country")).thenReturn("USA");
        when(request.getParameter("mobile")).thenReturn("6999345432");
        when(request.getParameter("gender")).thenReturn("Male");

        when(userDAO.isEmailValid("jaiganesh@gmail.com")).thenReturn(true);
        when(userDAO.isValidateName("jaiganesh123")).thenReturn(false); // Invalid name
        when(userDAO.checkEmailExists("jaiganesh@gmail.com")).thenReturn(false);

        // Mock the existing user
        User existingUser = new User(1, "Thirumalai", "Thirumalai@gmail.com", "India", "9892345432", "Male");
        when(userDAO.selectUser(1)).thenReturn(existingUser);
        userServlet.doPost(request, response);

        // Verify that the user was not updated
        verify(userDAO, never()).updateUser(any(User.class));
        verify(request).setAttribute("error", "Invalid name. Names should only contain letters and spaces.");

        // Verify that the user was redirected to the form page
        verify(request.getRequestDispatcher("user-form.jsp")).forward(request, response);
        System.out.println("Negative - TestCase UpdateUserWithInvalidName() passed!");
    }

//Negative - Testcase UpdateUserWithInvalidEmail
    @Test
    public void testUpdateUserWithInvalidEmail() throws ServletException, IOException, SQLException {

        when(request.getServletPath()).thenReturn("/update");
        when(request.getParameter("id")).thenReturn("1"); // Assuming the user id is 1
        when(request.getParameter("name")).thenReturn("jaiganesh");
        when(request.getParameter("email")).thenReturn("jaiganesh.com"); // Invalid email
        when(request.getParameter("country")).thenReturn("USA");
        when(request.getParameter("mobile")).thenReturn("6999345432");
        when(request.getParameter("gender")).thenReturn("Male");

        when(userDAO.isEmailValid("jaiganesh.com")).thenReturn(false); // Invalid email
        when(userDAO.isValidateName("jaiganesh")).thenReturn(true);
        when(userDAO.checkEmailExists("jaiganesh@gmail.com")).thenReturn(false);

        // Mock the existing user
        User existingUser = new User(1, "Thirumalai", "Thirumalai@gmail.com", "India", "9892345432", "Male");
        when(userDAO.selectUser(1)).thenReturn(existingUser);

        userServlet.doPost(request, response);

        // Verify that the user was not updated
        verify(userDAO, never()).updateUser(any(User.class));
        verify(request).setAttribute("error", "Please enter a valid email address. Sample@gmail.com");
        verify(request.getRequestDispatcher("user-form.jsp")).forward(request, response);
        System.out.println("Negative - TestCase UpdateUserWithInvalidEmail() passed!");
    }

       
//Positive - TestCase ShowNewForm
    @Test
    public void TestShowNewForm() throws ServletException, IOException {
    	
    	 when(request.getServletPath()).thenReturn("/new");
    	 userServlet.doPost(request, response);
    	 
    	 //verify that the request was dispatched to the correct JSP
    	 verify(request.getRequestDispatcher("user-form.jsp")).forward(request, response);	
         System.out.println("Positive - TestCase ShowNewForm() passed!");
    }
    
//Positive - TestCase ShowEditForm
    @Test
    public void TestShowEditForm() throws ServletException, IOException {

        when(request.getServletPath()).thenReturn("/edit");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // Mock the userDAO object
        User existingUser = new User(1, "Thirumalai", "Thirumalai@gmail.com", "India", "9892345432", "Male");
        when(userDAO.selectUser(1)).thenReturn(existingUser);
        userServlet.doPost(request, response);

     // Capture the user attribute that is set on the request
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(request).setAttribute(eq("user"), userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        
        assertNotNull(capturedUser, "User should not be null");
        assertEquals(existingUser.getName(), capturedUser.getName());
        assertEquals(existingUser.getEmail(), capturedUser.getEmail());
        assertEquals(existingUser.getCountry(), capturedUser.getCountry());
        assertEquals(existingUser.getMobile(), capturedUser.getMobile());
        assertEquals(existingUser.getGender(), capturedUser.getGender());

        verify(request.getRequestDispatcher("user-form.jsp")).forward(request, response);
        System.out.println("Positive - TestCase ShowEditForm() passed!");
    }

//Positive - TestCase DeleteUser
    @Test
    public void TestdeleteUser() throws ServletException, IOException {
    	
    	when(request.getServletPath()).thenReturn("/delete");
    	when(request.getParameter("id")).thenReturn("1");
    	
    	// Mock the userDAO object
        User existingUser = new User(1, "Thirumalai", "Thirumalai@gmail.com", "India", "9892345432", "Male");
        when(userDAO.selectUser(1)).thenReturn(existingUser);
        
        userServlet.doPost(request, response);
        
        verify(userDAO).deleteUser(1);
        verify(response).sendRedirect("list");
        System.out.println("Positive - TestCase DeleteUser() passed!");
         	
    }
    
//Positive - TestCase ListUser
    @Test
    public void testListUser() throws ServletException, IOException, SQLException {
        
        when(request.getServletPath()).thenReturn("/listuser");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        
        // Create a list of users to simulate the database response
        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "Lokesh", "Lokesh@example.com", "UK", "8909786670", "Male"));
        userList.add(new User(2, "Jane", "jane@example.com", "Canada", "0987654321", "Female"));
        
        // Mock the DAO to return the user list
        when(userDAO.selectAllUsers()).thenReturn(userList);
        userServlet.doPost(request, response);
        
        // Capture the request dispatcher
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(request).getRequestDispatcher(pathCaptor.capture());
        
        // Verify that the listUser attribute is set correctly
        ArgumentCaptor<List> attributeCaptor = ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("listUser"), attributeCaptor.capture());
        
        // Retrieve the captured attribute value
        List<User> capturedUserList = attributeCaptor.getValue();
        assertNotNull(capturedUserList);
        assertEquals(userList, capturedUserList);
        
        verify(request.getRequestDispatcher("user-list.jsp")).forward(request, response);
        System.out.println("Positive - TestCase ListUser() passed!");
    }  
}
