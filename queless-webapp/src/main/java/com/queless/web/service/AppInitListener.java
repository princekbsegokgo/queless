package com.queless.web.service;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Creates a single, application-wide QueueManager on startup so every
 * servlet (and every user's browser session) sees the same live queue
 * state - equivalent to the shared Room database in the Android version.
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    public static final String QUEUE_MANAGER_ATTR = "queueManager";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute(QUEUE_MANAGER_ATTR, new QueueManager());
    }
}
