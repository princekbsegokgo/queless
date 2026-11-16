package com.queless.web.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** All staff data and queue mutations require login; all staff POSTs require CSRF. */
@WebFilter(urlPatterns = {"/dashboard", "/callnext", "/togglequeue", "/complete"})
public class StaffAuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest in, ServletResponse out, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) in;
        HttpServletResponse response = (HttpServletResponse) out;
        response.setHeader("Cache-Control", "no-store");
        if (!RequestSecurity.staffAuthenticated(request)) {
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                response.sendRedirect(request.getContextPath() + "/staff/login");
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Staff login required");
            }
            return;
        }
        if ("POST".equalsIgnoreCase(request.getMethod())
                && !RequestSecurity.validCsrf(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid form token");
            return;
        }
        chain.doFilter(in, out);
    }
}
