package com.secure_web.servlets;

import io.github.cdimascio.dotenv.Dotenv;  // Import dotenv
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Logger;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            // Secure Version: Load environment variables securely from the .env file
            Dotenv dotenv = Dotenv.load();  // Load the .env file
            String dbUrl = dotenv.get("DB_URL");
            String dbUsername = dotenv.get("DB_USERNAME");
            String dbPassword = dotenv.get("DB_PASSWORD");

            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the MySQL database using the environment variables
            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);



            // Insecure Version (Only the changed part is commented out):
            // ====================================================
            //     // Insecure: Hardcoded database credentials
            //     String dbUrl = "jdbc:mysql://localhost:3306/secureweb";
            //     String dbUsername = "root";
            //     String dbPassword = "root";
            // ====================================================



            // Prepare the SQL query with user input
            String sql = "SELECT username, role FROM Users WHERE username = ? AND password = SHA2(?, 256)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Check if a user with the given credentials exists
            if (rs.next()) {
                String role = rs.getString("role");

                // Create a session and store user details
                // Get the current session, if it exists
                HttpSession session = request.getSession(false); // Don't create a new session if it doesn't exist

                // Problematic code: Session fixation vulnerability.
                // The session ID remains the same after logout, allowing attackers to hijack the session.
                // session.invalidate();  // Invalidate the session

                if (session != null) {
                    // Invalidate the session to clear all session attributes
                    session.invalidate(); // Invalidate the session
                }

                // Create a new session to avoid session fixation (important for security)
                session = request.getSession(true); // Create a new session for the user
                session.setAttribute("username", username);
                session.setAttribute("role", role);
                session.setMaxInactiveInterval(5 * 60); // Set session timeout to 5 minutes (300 seconds)

                // Redirect the user to the welcome page
                response.sendRedirect("welcome");
            } else {
                response.getWriter().println("Invalid credentials.");
            }



        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("MySQL Driver not found.");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
