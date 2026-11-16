package com.queless.web.servlet;

import com.queless.web.model.Ticket;
import com.queless.web.service.AppInitListener;
import com.queless.web.service.QueueManager;
import com.queless.web.security.RequestSecurity;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Public patient flow: choose a department, join, and get a private tracking link. */
@WebServlet("/joinqueue")
public class JoinQueueServlet extends HttpServlet {
    private QueueManager queueManager;

    @Override public void init() {
        queueManager = (QueueManager) getServletContext()
                .getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("queues", queueManager.getQueuesForBusiness(1L));
        req.setAttribute("csrfToken", RequestSecurity.csrfToken(req));
        req.setAttribute("waitingByQueue", java.util.Map.of(
                1L, queueManager.getQueueStatus(1L).size(),
                2L, queueManager.getQueueStatus(2L).size(),
                3L, queueManager.getQueueStatus(3L).size()
        ));
        req.setAttribute("business", queueManager.getAllBusinesses().stream().findFirst().orElse(null));
        resp.setHeader("Cache-Control", "no-store");
        req.getRequestDispatcher("/WEB-INF/views/patient-dashboard.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!RequestSecurity.validCsrf(req)) {
            resp.sendError(403, "Invalid form token"); return;
        }
        long queueId;
        try { queueId = Long.parseLong(req.getParameter("queueId")); }
        catch (Exception e) { resp.sendError(400, "Choose a queue"); return; }

        String name = req.getParameter("userName");
        if (name == null || name.isBlank() || name.length() > 80) {
            resp.sendRedirect(req.getContextPath() + "/joinqueue?invalid=1"); return;
        }

        // One active ticket per browser session prevents accidental duplicates.
        String previousToken = (String) req.getSession().getAttribute("myTicketToken");
        Ticket previous = queueManager.getTicketByAccessToken(previousToken).orElse(null);
        if (previous != null && (previous.getStatus() == Ticket.Status.WAITING
                || previous.getStatus() == Ticket.Status.NOTIFIED
                || previous.getStatus() == Ticket.Status.SERVING)) {
            resp.sendRedirect(req.getContextPath() + "/status?ticket=" + previousToken);
            return;
        }

        try {
            Ticket ticket = queueManager.joinQueue(queueId, name);
            req.getSession().setAttribute("myTicketToken", ticket.getAccessToken());
            resp.sendRedirect(req.getContextPath() + "/status?ticket=" + ticket.getAccessToken());
        } catch (IllegalStateException ex) {
            resp.sendRedirect(req.getContextPath() + "/joinqueue?paused=1");
        }
    }
}
