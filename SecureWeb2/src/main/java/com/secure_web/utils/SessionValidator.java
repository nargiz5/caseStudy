package com.secure_web.utils;

import java.io.*;
import java.util.Base64;

public class SessionValidator {

    public static SessionData deserializeSession(String serializedData) throws IOException, ClassNotFoundException {
        byte[] decodedData = Base64.getDecoder().decode(serializedData);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(decodedData);
             ObjectInputStream ois = new ObjectInputStream(bais)) {

            Object obj = ois.readObject();
            if (obj instanceof SessionData) {
                SessionData sessionData = (SessionData) obj;

                // Validate deserialized username and role
                if (!SessionData.isValidUsername(sessionData.getUsername())) {
                    throw new SecurityException("Invalid username in deserialized session data.");
                }

                if (!SessionData.isValidRole(sessionData.getRole())) {
                    throw new SecurityException("Invalid role in deserialized session data.");
                }

                return sessionData;
            } else {
                throw new SecurityException("Deserialized object is not of type SessionData.");
            }
        }
    }
}
