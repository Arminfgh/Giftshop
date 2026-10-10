package com.giftshop.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Blocks access to /admin/** unless the user has logged in.
 * The login page itself is excluded when the interceptor is registered.
 */
public class AdminInterceptor implements HandlerInterceptor {

    public static final String SESSION_KEY = "ADMIN_LOGGED_IN";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && Boolean.TRUE.equals(session.getAttribute(SESSION_KEY));
        if (loggedIn) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/admin/login");
        return false;
    }
}
