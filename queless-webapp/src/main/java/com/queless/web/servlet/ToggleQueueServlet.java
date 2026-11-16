package com.queless.web.servlet;

import com.queless.web.service.AppInitListener;
import com.queless.web.service.QueueManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Pause/resume queue intake and operator calls.
 * Demonstration endpoint only: must be protected by staff authentication
 * before use with real patients.
 */
@WebServlet("/togglequeue")
public class ToggleQueueServlet extends HttpServlet {
    private QueueManager queueManager;

    @Override
    public void init() {
        queueManager = (QueueManager) getServletContext()
                .getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        boolean open = "resume".equals(req.getParameter("action"));
        long queueId;
        try { queueId = Long.parseLong(req.getParameter("queueId")); }
        catch (Exception e) { resp.sendError(400, "Choose a queue"); return; }
        if (!"pause".equals(req.getParameter("action"))
                && !"resume".equals(req.getParameter("action"))) {
            resp.sendError(400, "Invalid action"); return;
        }
        if (!queueManager.setQueueOpen(queueId, open)) {
            resp.sendError(404); return;
        }
        resp.sendRedirect(req.getContextPath()
                + "/dashboard?queueId=" + queueId + "&result=" + (open ? "resumed" : "paused"));
    }
}
