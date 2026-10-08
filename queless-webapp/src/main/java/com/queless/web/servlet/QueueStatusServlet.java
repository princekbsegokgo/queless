package com.queless.web.servlet;

import com.queless.web.model.Queue;
import com.queless.web.model.Ticket;
import com.queless.web.service.AppInitListener;
import com.queless.web.service.QueueManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * RQ02: shows the user's live position and any notification message set
 * by QueueManager/NotificationService. The page includes a short
 * meta-refresh (see status.jsp) so it re-polls this servlet automatically,
 * simulating a real-time update without needing WebSockets for the MVP.
 */
@WebServlet("/status")
public class QueueStatusServlet extends HttpServlet {

    private QueueManager queueManager;

    @Override
    public void init() {
        queueManager = (QueueManager) getServletContext().getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long ticketId = Long.parseLong(req.getParameter("ticketId"));
        Optional<Ticket> ticketOpt = queueManager.getTicket(ticketId);

        if (ticketOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/joinqueue");
            return;
        }

        Ticket ticket = ticketOpt.get();
        Optional<Queue> queueOpt = queueManager.getQueue(ticket.getQueueId());
        int nowServing = queueOpt.map(Queue::getCurrentServingPosition).orElse(0);
        int estimatedWaitMinutes = queueManager.estimateWaitTimeMinutes(ticket.getQueueId(), ticket.getPosition());

        req.setAttribute("ticket", ticket);
        req.setAttribute("nowServing", nowServing);
        req.setAttribute("estimatedWaitMinutes", estimatedWaitMinutes);
        req.getRequestDispatcher("/status.jsp").forward(req, resp);
    }
}
