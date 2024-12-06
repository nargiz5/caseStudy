package com.secure_web.servlets;

import io.github.cdimascio.dotenv.Dotenv;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.UUID;

public class UploadProfilePictureServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "profile_pics";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Ensure the user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get the uploaded file
        Part filePart = request.getPart("profile_picture");
        if (filePart == null || filePart.getSize() == 0) {
            response.getWriter().println("No file uploaded. Please select a file.");
            return;
        }

        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        // Validate file type (only .jpg, .jpeg, .png)
        if (!fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png)$")) {
            response.getWriter().println("Invalid file type. Only .png, .jpg, and .jpeg are allowed.");
            return;
        }

        // Get the username from session
        String username = (String) session.getAttribute("username");

        // Generate a random unique file name for the new profile picture
        String newFileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf('.'));

        // Prepare the file path to save the uploaded image
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir(); // Create directory if it doesn't exist

        File newFile = new File(uploadDir, newFileName);
        filePart.write(newFile.getAbsolutePath());

        // Insecure Version (Only the changed part is commented out):
        // ====================================================
        //     // Insecure: Hardcoded database credentials
        //     Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/secureweb", "root", "root");
        // ====================================================

        // Secure Version: Using environment variables for database credentials
        try {
            Dotenv dotenv = Dotenv.load();  // Load the .env file securely
            String dbUrl = dotenv.get("DB_URL");
            String dbUsername = dotenv.get("DB_USERNAME");
            String dbPassword = dotenv.get("DB_PASSWORD");

            // Secure database connection
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
                // Retrieve the current profile picture from the database
                String currentPicture = null;
                String selectQuery = "SELECT profile_picture FROM users WHERE username = ?";
                try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                    selectStmt.setString(1, username);
                    ResultSet rs = selectStmt.executeQuery();
                    if (rs.next()) {
                        currentPicture = rs.getString("profile_picture");
                    }
                }

                // Update the profile picture in the database
                String updateQuery = "UPDATE users SET profile_picture = ? WHERE username = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, newFileName);
                    updateStmt.setString(2, username);
                    updateStmt.executeUpdate();
                }

                // Delete the old profile picture from the server if it's not the default
                if (currentPicture != null && !currentPicture.equals("default.jpg")) {
                    File oldFile = new File(uploadDir, currentPicture);
                    if (oldFile.exists()) oldFile.delete();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                response.getWriter().println("Error updating profile picture in the database.");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error loading environment variables.");
            return;
        }

        // Redirect to the welcome page after successful upload
        response.sendRedirect("welcome");
    }
}
