package com.secure_web.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the current session, if it exists
        HttpSession session = request.getSession(false); // Don't create a new session if it doesn't exist

        if (session != null) {
            // Invalidate the session to clear all session attributes
            session.invalidate();
        }

        // Mitigate session fixation vulnerability by creating a new session after invalidation
        HttpSession newSession = request.getSession(true); // Create a new session for the user

        // Optionally, configure secure cookie flags for the new session ID
        Cookie sessionCookie = new Cookie("JSESSIONID", newSession.getId());
        sessionCookie.setHttpOnly(true);  // Prevent client-side scripts from accessing the cookie
        sessionCookie.setSecure(request.isSecure()); // Set Secure flag only for HTTPS connections
        sessionCookie.setPath("/");       // Make the cookie accessible for the entire application
        response.addCookie(sessionCookie); // Add the cookie to the response

        // Redirect the user to the login page after logout
        response.sendRedirect("login.jsp");
    }
}
