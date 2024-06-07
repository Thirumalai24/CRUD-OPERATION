package com.Thapovan.usermanagement.dao;


import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ConnPoolListeners implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger(ConnPoolListeners.class);
    private DataSource ds;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    				
        ds = (DataSource) sce.getServletContext().getAttribute("Conn-pool"); // Retrieve DataSource object from servlet context attributes
        if (ds == null) {  							// If ds object is not found in servlet context
            ds = DbDriverManager.getInstance().initializeDataSource(); //initialize s using dgmanager instance
            sce.getServletContext().setAttribute("Conn-pool", ds);   // Set DataSource object as attribute in servlet context
            logger.debug("Connection Pool is initialized");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    	
    	// Retrieve DataSource object from servlet context attributes
        HikariDataSource ds = (HikariDataSource) sce.getServletContext().getAttribute("Conn-pool"); 
        if (ds != null) {
            ds.close();
            logger.debug("Connection Pool is closed");
        }
    }
}