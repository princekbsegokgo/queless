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

/**
 * Showcase mode: staff dashboard is publicly reachable from the Staff Portal button.
 * This is intentionally NOT authorization. Keep CSRF checks on dashboard actions
 * so other websites cannot silently submit queue changes in a viewer's browser.
 * Restore real staff authentication before using real patient data.
 */
@WebFilter(urlPatterns = {"/dashboard", "/callnext", "/togglequeue", "/complete", "/demo/load"})
public class StaffAuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest in, ServletResponse out, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) in;
        HttpServletResponse response = (HttpServletResponse) out;
        response.setHeader("Cache-Control", "no-store");
        if ("POST".equalsIgnoreCase(request.getMethod())
                && !RequestSecurity.validCsrf(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid form token");
            return;
        }
        chain.doFilter(in, out);
    }
}
