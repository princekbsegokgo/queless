package com.queless.web.servlet;

import com.queless.web.security.RequestSecurity;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/staff/login")
public class StaffLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (RequestSecurity.staffAuthenticated(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        resp.setHeader("Cache-Control", "no-store");
        req.setAttribute("staffConfigured", RequestSecurity.configured());
        req.setAttribute("csrfToken", RequestSecurity.csrfToken(req));
        req.getRequestDispatcher("/WEB-INF/views/staff-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!RequestSecurity.validCsrf(req)) {
            resp.sendError(403, "Invalid form token");
            return;
        }
        if (!RequestSecurity.configured()) {
            resp.sendRedirect(req.getContextPath() + "/staff/login?setup=1");
            return;
        }
        HttpSession session = req.getSession();
        int attempts = session.getAttribute("staffLoginAttempts") instanceof Integer
                ? (Integer) session.getAttribute("staffLoginAttempts") : 0;
        if (attempts >= 5) {
            resp.sendRedirect(req.getContextPath() + "/staff/login?locked=1");
            return;
        }
        if (RequestSecurity.validStaffPassword(req.getParameter("password"))) {
            req.changeSessionId();
            session.removeAttribute("staffLoginAttempts");
            session.setAttribute(RequestSecurity.STAFF_SESSION_ATTRIBUTE, Boolean.TRUE);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            session.setAttribute("staffLoginAttempts", attempts + 1);
            resp.sendRedirect(req.getContextPath() + "/staff/login?error=1");
        }
    }
}
