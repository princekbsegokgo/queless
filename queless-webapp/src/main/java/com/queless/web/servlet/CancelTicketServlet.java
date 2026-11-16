package com.queless.web.servlet;

import com.queless.web.security.RequestSecurity;
import com.queless.web.service.AppInitListener;
import com.queless.web.service.QueueManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Patients can cancel a waiting ticket, but not a ticket already being served. */
@WebServlet("/cancel")
public class CancelTicketServlet extends HttpServlet {
    private QueueManager queueManager;

    @Override public void init() {
        queueManager = (QueueManager) getServletContext()
                .getAttribute(AppInitListener.QUEUE_MANAGER_ATTR);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!RequestSecurity.validCsrf(req)) {
            resp.sendError(403, "Invalid form token"); return;
        }
        String token = req.getParameter("ticket");
        if (queueManager.cancelTicketByAccessToken(token)) {
            req.getSession().removeAttribute("myTicketToken");
            resp.sendRedirect(req.getContextPath() + "/status?ticket=" + token + "&cancelled=1");
        } else {
            resp.sendRedirect(req.getContextPath() + "/status?ticket=" +
                    java.net.URLEncoder.encode(token == null ? "" : token,
                            java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}
