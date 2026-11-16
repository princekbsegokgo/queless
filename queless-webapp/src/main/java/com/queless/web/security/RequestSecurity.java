package com.queless.web.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Session-backed form CSRF protection and staff access checks.
 * Configure the password as a Render secret: QUELESS_STAFF_PASSWORD.
 */
public final class RequestSecurity {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String TOKEN_ATTRIBUTE = "quelessCsrfToken";
    public static final String STAFF_SESSION_ATTRIBUTE = "quelessStaffAuthenticated";

    private RequestSecurity() {}

    public static String csrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        synchronized (session) {
            String token = (String) session.getAttribute(TOKEN_ATTRIBUTE);
            if (token == null) {
                byte[] bytes = new byte[32];
                RANDOM.nextBytes(bytes);
                token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                session.setAttribute(TOKEN_ATTRIBUTE, token);
            }
            return token;
        }
    }

    public static boolean validCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String expected = session == null ? null : (String) session.getAttribute(TOKEN_ATTRIBUTE);
        String supplied = request.getParameter("_csrf");
        return constantTimeEquals(expected, supplied);
    }

    public static String configuredStaffPassword() {
        String password = System.getenv("QUELESS_STAFF_PASSWORD");
        return password == null || password.isBlank() ? null : password;
    }

    public static boolean configured() {
        return configuredStaffPassword() != null;
    }

    public static boolean staffAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null
                && Boolean.TRUE.equals(session.getAttribute(STAFF_SESSION_ATTRIBUTE));
    }

    public static boolean validStaffPassword(String supplied) {
        return constantTimeEquals(configuredStaffPassword(), supplied);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }
}
