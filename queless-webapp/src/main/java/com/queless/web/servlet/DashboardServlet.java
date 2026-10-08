package com.queless.web.servlet;

import com.queless.web.service.AppInitListener;
import com.queless.web.service.QueueManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * RQ03: business-facing dashboard. Shows the live waiting list for the
 * demo queue and lets the business advance the queue with "Call Next".
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private QueueManager queueManager;
    private static final long DEMO_QUEUE_ID = 1L;

    @Override
    public void init() {
        queueManager = (QueueManager) getServletContext().getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var queue = queueManager.getQueue(DEMO_QUEUE_ID).orElse(null);
        var waitingTickets = queueManager.getQueueStatus(DEMO_QUEUE_ID);

        req.setAttribute("queue", queue);
        req.setAttribute("waitingTickets", waitingTickets);
        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
