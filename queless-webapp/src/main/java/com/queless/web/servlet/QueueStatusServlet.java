package com.queless.web.servlet;

import com.queless.web.model.Queue;
import com.queless.web.model.Ticket;
import com.queless.web.security.RequestSecurity;
import com.queless.web.service.AppInitListener;
import com.queless.web.service.QueueManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/** A private, bookmarkable ticket page which refreshes automatically. */
@WebServlet("/status")
public class QueueStatusServlet extends HttpServlet {
    private QueueManager queueManager;

    @Override public void init() {
        queueManager = (QueueManager) getServletContext()
                .getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("ticket");
        if (token == null || token.isBlank()) {
            token = (String) req.getSession().getAttribute("myTicketToken");
        }
        Optional<Ticket> found = queueManager.getTicketByAccessToken(token);
        if (found.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/joinqueue?notfound=1"); return;
        }
        Ticket ticket = found.get();
        Queue queue = queueManager.getQueue(ticket.getQueueId()).orElse(null);
        req.setAttribute("ticket", ticket);
        req.setAttribute("queue", queue);
        req.setAttribute("nowServing", queue == null ? 0 : queue.getCurrentServingPosition());
        req.setAttribute("peopleAhead", queueManager.getPeopleAhead(ticket));
        req.setAttribute("estimatedWaitMinutes", queueManager.estimateWaitMinutes(ticket));
        req.setAttribute("csrfToken", RequestSecurity.csrfToken(req));
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Referrer-Policy", "no-referrer");
        req.getRequestDispatcher("/status.jsp").forward(req, resp);
    }
}
