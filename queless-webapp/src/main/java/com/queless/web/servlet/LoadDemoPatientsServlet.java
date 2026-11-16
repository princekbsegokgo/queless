package com.queless.web.servlet;

import com.queless.web.service.AppInitListener;
import com.queless.web.service.QueueManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** One-click sample queue for class demos, CSRF protected by StaffAuthFilter. */
@WebServlet("/demo/load")
public class LoadDemoPatientsServlet extends HttpServlet {
    private QueueManager manager;

    @Override
    public void init() {
        manager = (QueueManager) getServletContext()
                .getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        long id;
        try {
            id = Long.parseLong(request.getParameter("queueId"));
        } catch (Exception exception) {
            response.sendError(400, "Choose a queue");
            return;
        }
        boolean loaded = manager.loadSampleTickets(id);
        response.sendRedirect(request.getContextPath() + "/dashboard?queueId=" + id
                + "&result=" + (loaded ? "demoLoaded" : "demoSkipped"));
    }
}
