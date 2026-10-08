package com.queless.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * RQ05: lets the user switch between English and Setswana at any time.
 * The choice is stored in the session and read by every JSP page via
 * &lt;fmt:setLocale value="${sessionScope.lang}"/&gt;.
 */
@WebServlet("/language")
public class LanguageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String lang = req.getParameter("lang");
        if ("tn".equals(lang) || "en".equals(lang)) {
            HttpSession session = req.getSession();
            session.setAttribute("lang", lang);
        }
        String returnTo = req.getParameter("returnTo");
        resp.sendRedirect(req.getContextPath() + (returnTo != null ? returnTo : "/index.jsp"));
    }
}
