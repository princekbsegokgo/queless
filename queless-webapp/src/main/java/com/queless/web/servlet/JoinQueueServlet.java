package com.queless.web.servlet;

import com.queless.web.model.Queue;
import com.queless.web.service.AppInitListener;
import com.queless.web.service.QueueManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * RQ01: lists the available queues (GET) and lets a user reserve a
 * position remotely (POST), without needing to be physically present.
 */
@WebServlet("/joinqueue")
public class JoinQueueServlet extends HttpServlet {

    private QueueManager queueManager;

    @Override
    public void init() {
        queueManager = (QueueManager) getServletContext().getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Demo business seeded by QueueManager on startup.
        long demoBusinessId = 1L;
        List<Queue> queues = queueManager.getQueuesForBusiness(demoBusinessId);
        req.setAttribute("queues", queues);
        req.getRequestDispatcher("/join.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long queueId = Long.parseLong(req.getParameter("queueId"));
        String userName = req.getParameter("userName");
        if (userName == null || userName.isBlank()) {
            userName = "Guest";
        }

        var ticket = queueManager.joinQueue(queueId, userName);

        resp.sendRedirect(req.getContextPath() + "/status?ticketId=" + ticket.getTicketId());
    }
}
