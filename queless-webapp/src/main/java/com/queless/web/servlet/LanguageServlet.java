package com.queless.web.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Local English/Setswana switch with a safe, allowlisted return destination. */
@WebServlet("/language")
public class LanguageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String lang = req.getParameter("lang");
        if ("en".equals(lang) || "tn".equals(lang)) {
            req.getSession().setAttribute("lang", lang);
        }
        String path = req.getParameter("returnTo");
        if (path == null || !java.util.Set.of(
                "/", "/index.jsp", "/joinqueue", "/status",
                "/dashboard", "/staff/login").contains(path)) {
            path = "/index.jsp";
        }
        String ticket = req.getParameter("ticket");
        if ("/status".equals(path) && ticket != null
                && ticket.matches("[A-Za-z0-9_-]{32}")) {
            path += "?ticket=" + URLEncoder.encode(ticket, StandardCharsets.UTF_8);
        }
        resp.sendRedirect(req.getContextPath() + path);
    }
}
