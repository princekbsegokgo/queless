package com.queless.web.servlet;

import com.queless.web.service.AppInitListener;
import com.queless.web.service.QueueManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Advance one ticket when the queue is open. POST-only operator action. */
@WebServlet("/callnext")
public class CallNextServlet extends HttpServlet {
    private QueueManager queueManager;

    @Override
    public void init() {
        queueManager = (QueueManager) getServletContext()
                .getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        long queueId;
        try { queueId = Long.parseLong(req.getParameter("queueId")); }
        catch (Exception e) { resp.sendError(400, "Choose a queue"); return; }
        boolean advanced = queueManager.callNextTicket(queueId);
        resp.sendRedirect(req.getContextPath()
                + "/dashboard?queueId=" + queueId + "&result=" + (advanced ? "called" : "noNext"));
    }
}
