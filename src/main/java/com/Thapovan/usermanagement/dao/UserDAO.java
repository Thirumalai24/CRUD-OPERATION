package com.Thapovan.usermanagement.dao;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDAO {
		private static final Logger logger = LogManager.getLogger(UserDAO.class);	
    
    	private static final UserDAO instance = new UserDAO(); 		
    
    	private static final DbDriverManager driverManager = DbDriverManager.getInstance();
    
        private static final String INSERT_USERS_SQL = "{call InsertUser(?, ?, ?, ?, ?)}";
        private static final String SELECT_USER_BY_ID = "{call GetUserById(?)}";
        private static final String SELECT_ALL_USERS = "{call GetAllUsers()}";
        private static final String DELETE_USERS_SQL = "{call DeleteUserById(?)}";
        private static final String UPDATE_USERS_SQL = "{call UpdateUser(?, ?, ?, ?, ?, ?)}";
        private static final String EMAIL_VALIDATE = "{call CheckEmailExists(?,?)}";
        
        private int insertQueryCount = 0;
        private int selectAllQueryCount = 0;
        private int deleteQueryCount = 0;
        private int updateQueryCount = 0;
        private int emailExistsQueryCount = 0;
        
        private UserDAO() {  // private constructor to access the instance
        	
        }		

        public static UserDAO getInstance() {
            return instance;
        }

        public boolean isEmailValid(String email) {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }

        public  boolean isValidateName(String name) {
            String regex = "^[a-zA-Z ]+$";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(name);
            return matcher.find();
        }
        //Insert user
        public void insertUser(User user) throws SQLException {
            	   Connection connection = driverManager.getConnection();
                try (CallableStatement callableStatement = connection.prepareCall(INSERT_USERS_SQL)) {
                    callableStatement.setString(1, user.getName());
                    callableStatement.setString(2, user.getEmail());
                    callableStatement.setString(3, user.getCountry());
                    callableStatement.setString(4, user.getMobile());
                    callableStatement.setString(5, user.getGender());
                    callableStatement.execute();
                    insertQueryCount++;
                    logger.debug("User inserted successfully - Total Insert query executed: " + insertQueryCount);
                }
            catch (SQLException e) 
            {
                logger.error("Error inserting user: " + user.getName(),e);
            } 
            finally 
            {
                    driverManager.releaseConnection(connection);
            }
        }
        //Edit User by ID
        public User selectUser(int id) {
            User user = null;
            	Connection connection = driverManager.getConnection();
                try (CallableStatement callableStatement = connection.prepareCall(SELECT_USER_BY_ID)) {
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
                }
            catch (SQLException e) 
            {
                logger.error("Error selecting user.", e);
            }
            finally 
            {
            	driverManager.releaseConnection(connection);
            }
            return user;
        }
        
        //Select All users
        public List<User> selectAllUsers() {
            List<User> users = new ArrayList<>();
            	Connection connection = driverManager.getConnection();
                try (CallableStatement callableStatement = connection.prepareCall(SELECT_ALL_USERS)) {
                    ResultSet rs = callableStatement.executeQuery();
                    selectAllQueryCount++;
                   logger.debug("User Displayed Successfully - Total Select query executed:" + selectAllQueryCount);
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
                logger.error("Error selecting all users.", e);
            } 
            finally
            {
            	driverManager.releaseConnection(connection);
            }
            return users;
        }
        //
        //Delete Users
        public boolean deleteUser(int id) {
            boolean rowDeleted = false;
            	Connection connection = driverManager.getConnection();
                try (CallableStatement callableStatement = connection.prepareCall(DELETE_USERS_SQL)) {
                    callableStatement.setInt(1, id);
                    rowDeleted = callableStatement.executeUpdate() > 0;
                    deleteQueryCount++;
                    logger.debug("User Deleted Successfully- Total delete query executed:" + deleteQueryCount);
                }
            catch (SQLException e) 
            {
                logger.error("Error deleting user.", e);
            } 
            finally 
            {
            	driverManager.releaseConnection(connection);
            }
            return rowDeleted;
        }
        
        //Update user
        public boolean updateUser(User user) {
            boolean rowUpdated = false;
            	Connection connection = driverManager.getConnection();
                try (CallableStatement callableStatement = connection.prepareCall(UPDATE_USERS_SQL)) {
                    callableStatement.setInt(1, user.getId());
                    callableStatement.setString(2, user.getName());
                    callableStatement.setString(3, user.getEmail());
                    callableStatement.setString(4, user.getCountry());
                    callableStatement.setString(5, user.getMobile());
                    callableStatement.setString(6, user.getGender());
                    int updateCount = callableStatement.executeUpdate();
                    rowUpdated = (updateCount > 0);
                    updateQueryCount++;
                    logger.debug("User Updated Successfully - Total update Query:" + updateQueryCount);
                }
            catch (SQLException e) 
            {
              logger.error("Error updating user.", e);
            } 
            finally
            {
            	driverManager.releaseConnection(connection);
            }
            return rowUpdated;
        }
        
        //Email validation
        public boolean checkEmailExists(String email) {
            boolean emailExists = false;
            	Connection connection = driverManager.getConnection();
                try (CallableStatement callableStatement = connection.prepareCall(EMAIL_VALIDATE)) {
                    callableStatement.setString(1, email);
                    callableStatement.registerOutParameter(2, java.sql.Types.BOOLEAN);
                    callableStatement.execute();
                    emailExists = callableStatement.getBoolean(2);
                    if (emailExists) {
                        emailExistsQueryCount++;
                       logger.debug("Email Already Exists - Total Check email query executed:" + emailExistsQueryCount);
                    }
                }
            catch (SQLException e) 
            {
               logger.error("Error checking email existence.", e);
            } 
            finally 
            {
            	driverManager.releaseConnection(connection);
            }
            return emailExists;
        }
    }
