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

/** Patients can only join queues which are currently open. */
@WebServlet("/joinqueue")
public class JoinQueueServlet extends HttpServlet {
    private QueueManager queueManager;

    @Override
    public void init() {
        queueManager = (QueueManager) getServletContext()
                .getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Queue> queues = queueManager.getQueuesForBusiness(1L);
        req.setAttribute("queues", queues);
        req.getRequestDispatcher("/join.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        long queueId;
        try {
            queueId = Long.parseLong(req.getParameter("queueId"));
        } catch (NumberFormatException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid queue");
            return;
        }
        try {
            var ticket = queueManager.joinQueue(queueId, req.getParameter("userName"));
            resp.sendRedirect(req.getContextPath()
                    + "/status?ticketId=" + ticket.getTicketId());
        } catch (IllegalStateException ex) {
            resp.sendRedirect(req.getContextPath() + "/joinqueue?paused=1");
        }
    }
}
