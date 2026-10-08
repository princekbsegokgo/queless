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
 * RQ03: advances the demo queue by one. This is what triggers the RQ02
 * notification thresholds inside QueueManager.callNextTicket().
 */
@WebServlet("/callnext")
public class CallNextServlet extends HttpServlet {

    private QueueManager queueManager;
    private static final long DEMO_QUEUE_ID = 1L;

    @Override
    public void init() {
        queueManager = (QueueManager) getServletContext().getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        queueManager.callNextTicket(DEMO_QUEUE_ID);
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
