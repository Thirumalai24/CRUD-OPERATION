package com.Thapovan.usermanagement.dao;
 
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.InputStream;
import java.io.IOException;

public class UserDAO {

    private static final String INSERT_USERS_SQL = "{call InsertUser(?, ?, ?, ?, ?)}";
    private static final String SELECT_USER_BY_ID = "{call GetUserById(?)}";
    private static final String SELECT_ALL_USERS = "{call GetAllUsers()}";
    private static final String DELETE_USERS_SQL = "{call DeleteUserById(?)}";
    private static final String UPDATE_USERS_SQL = "{call UpdateUser(?, ?, ?, ?, ?, ?)}";
    private static final String EMAIL_VALIDATE   ="{call CheckEmailExists(?,?)}";
    
 // Database connection properties keys
    private static final String DB_DRIVER_CLASS="driver.class.name"; 
    private static final String DB_URL="db.url";
    private static final String DB_USERNAME="db.username";
    private static final String DB_PASSWORD="db.password";
    

    private static Connection connection = null;				// Connection object to hold the database connection
    private static Properties properties = new Properties();    // Properties object to store database connection properties
    private static BasicDataSource dataSource; 					// BasicDataSource object for connection pooling


    public static Connection getConnection() {
    	
					//Classname.class.getClassLoader().getResourcesAsStream("/path") --> expression is used to load a resource file, 
																	//specifically a properties file named "fileName.properties".																			    	
        try (InputStream input = UserDAO.class.getClassLoader().getResourceAsStream("database.properties")) {
        	
            properties.load(input);    				// Load database properties from the "database.properties" file

            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(properties.getProperty(DB_DRIVER_CLASS));
            dataSource.setUrl(properties.getProperty(DB_URL));
            dataSource.setUsername(properties.getProperty(DB_USERNAME));
            dataSource.setPassword(properties.getProperty(DB_PASSWORD));
            dataSource.setMinIdle(10);
            dataSource.setMaxIdle(100);

            connection = dataSource.getConnection(); // Use getConnection method to get a connection from the pool

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing database connection.", e);
        }

        return connection;
    }
    public boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validateName(String name) {
        String regex = "^[a-zA-Z ]+$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }

    public void insertUser(User user) throws SQLException {
        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(INSERT_USERS_SQL)) {

            callableStatement.setString(1, user.getName());
            callableStatement.setString(2, user.getEmail());
            callableStatement.setString(3, user.getCountry());
            callableStatement.setString(4, user.getMobile());
            callableStatement.setString(5, user.getGender());

            callableStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user.", e);
        }
    }

    //---Form edit using user id----	
    public User selectUser(int id) {
        User user = null;
        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(SELECT_USER_BY_ID)) {

            callableStatement.setInt(1, id);
            ResultSet rs = callableStatement.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                String mobile = rs.getString("mobile");
                String gender = rs.getString("gender");

                user = new User(id, name, email, country, mobile, gender);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error selecting user.", e);
        }
        return user;
    }

    	//---select all user---
    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(SELECT_ALL_USERS)) {

            ResultSet rs = callableStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                String mobile = rs.getString("mobile");
                String gender = rs.getString("gender");
                users.add(new User(id, name, email, country, mobile, gender));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error selecting all users.", e);
        }
        return users;
    }
    
    // --delete user by id----
    public boolean deleteUser(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(DELETE_USERS_SQL)) {

            callableStatement.setInt(1, id);
            rowDeleted = callableStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user.", e);
        }
        return rowDeleted;
    }

    public boolean updateUser(User user) throws SQLException {
        boolean rowUpdated = false;

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(UPDATE_USERS_SQL)) {

            callableStatement.setInt(1, user.getId());
            callableStatement.setString(2, user.getName());
            callableStatement.setString(3, user.getEmail());
            callableStatement.setString(4, user.getCountry());
            callableStatement.setString(5, user.getMobile());
            callableStatement.setString(6, user.getGender());

            int updateCount = callableStatement.executeUpdate();

            rowUpdated = (updateCount > 0);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user.", e);
        }

        return rowUpdated;
    }
    public boolean checkEmailExists(String email) {
        boolean emailExists = false;

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(EMAIL_VALIDATE)) {

            callableStatement.setString(1, email);
            callableStatement.registerOutParameter(2, java.sql.Types.BOOLEAN); // Register the output parameter

            callableStatement.execute();

            // Retrieve the output parameter value
            emailExists = callableStatement.getBoolean(2);

        } catch (SQLException e) {
            throw new RuntimeException("Error checking email existence.", e);
        }
        return emailExists;
    }

}
