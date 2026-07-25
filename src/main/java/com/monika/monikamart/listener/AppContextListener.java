package com.monika.monikamart.listener;

import com.monika.monikamart.util.DBUtil;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info(">>> MonikaMart Web Application Starting Up...");
        try {
            DBUtil.initialize();
            LOGGER.info(">>> MonikaMart Database and Connection Pool Ready.");
        } catch (Exception e) {
            LOGGER.severe(">>> Critical: Failed to initialize application context: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info(">>> MonikaMart Web Application Shutting Down...");
        DBUtil.closePool();
        LOGGER.info(">>> MonikaMart HikariCP Connection Pool cleanly closed.");
    }
}
