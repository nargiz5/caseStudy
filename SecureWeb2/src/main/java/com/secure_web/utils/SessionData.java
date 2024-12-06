package com.secure_web.utils;

import java.io.Serializable;

public class SessionData implements Serializable {
    private final String username;
    private final String role;

    public SessionData(String username, String role) {
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username format.");
        }
        this.username = username;

        if (!isValidRole(role)) {
            throw new IllegalArgumentException("Invalid role.");
        }
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    // Validate username using a regex (allow letters, numbers, and underscores, min length 3, max length 20)
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    // Validate role to ensure it only matches predefined roles
    public static boolean isValidRole(String role) {
        return "user".equals(role) || "admin".equals(role);
    }
}
