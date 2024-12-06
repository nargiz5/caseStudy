package com.secure_web.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class WelcomeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Ensure the user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String username = (String) session.getAttribute("username");

        String profilePicture = "profile_pics/default.jpg"; // Default profile picture

        // Secure Version: Using environment variables for database credentials
        try {
            // Load the environment variables from the .env file
            Dotenv dotenv = Dotenv.load();
            String dbUrl = dotenv.get("DB_URL");
            String dbUsername = dotenv.get("DB_USERNAME");
            String dbPassword = dotenv.get("DB_PASSWORD");


            // Insecure Code (before using dotenv):
            // try {
            //     Connection conn = DriverManager.getConnection(
            //         "jdbc:mysql://localhost:3306/secureweb",  // Hardcoded DB URL
            //         "root",  // Hardcoded DB username
            //         "root"   // Hardcoded DB password
            // );

            // Secure database connection
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
                String sql = "SELECT profile_picture FROM users WHERE username = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    profilePicture = rs.getString("profile_picture");
                    if (profilePicture == null || profilePicture.isEmpty()) {
                        profilePicture = "profile_pics/default.jpg"; // Fallback to default
                    } else {
                        profilePicture = "profile_pics/" + profilePicture; // Prepend 'profile_pics/' to the image file name
                    }
                } else {
                    System.out.println("No profile picture found in DB for username: " + username); // Print message if no result
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Log error if DB query fails
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error loading environment variables.");
            return;
        }

        // Set the profile picture and username as request attributes to be displayed
        request.setAttribute("username", username);
        request.setAttribute("profilePicture", profilePicture);

        // Forward the request to the JSP page
        RequestDispatcher dispatcher = request.getRequestDispatcher("welcome.jsp");
        dispatcher.forward(request, response);
    }
}
