package com.Thapovan.usermanagement.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DbDriverManager {

    private static final Logger logger = LogManager.getLogger(DbDriverManager.class);
    private static final DbDriverManager instance = new DbDriverManager();
    private HikariDataSource dataSource;
    private Connection connection;
    
    public int totalConnections = 0;
    public int totalConnectionsClosed = 0;

  

    private DbDriverManager() {
        initializeDataSource();   
    }

    public static synchronized DbDriverManager getInstance() {
        return instance;
    }

    public DataSource initializeDataSource() {
        try (InputStream input = DbDriverManager.class.getClassLoader().getResourceAsStream("database.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setDriverClassName(properties.getProperty("driver.class.name"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));

           
            config.setMinimumIdle(8);
            config.setMaximumPoolSize(10);
//            config.setConnectionTimeout(30000); // 30 seconds
//            config.setIdleTimeout(350000); // 5 minutes
//            config.setMaxLifetime(1200000); // 20 minutes
            

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            logger.error("Error loading database properties.", e);
        }
		return dataSource;
    }

    // Get connection
    public Connection getConnection()  {
        totalConnections++;
       logger.debug("Total Connections Open: " + totalConnections);
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
           logger.error("Error getting connection.", e);
        }
        return connection;
    }

    // Release connection
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                totalConnectionsClosed++;
                logger.debug("Connection closed. Total closed connections: " + totalConnectionsClosed);
            } catch (SQLException e) {
                logger.error("Error closing connection.", e);
            }
        }
    }

    // Log total connections
    public void logTotalConnections() {
        logger.debug("Total connections opened: " + totalConnections);
        logger.debug("Total connections closed: " + totalConnectionsClosed);
    }
}
