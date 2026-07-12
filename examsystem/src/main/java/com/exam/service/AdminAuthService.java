package com.exam.service;

/**
 * Very small authentication check that gates access to the Question Bank
 * Management screen behind an "admin" username/password.
 *
 * <p><b>Note:</b> this is intentionally simple for demonstration purposes
 * (a hard-coded in-memory credential). A production system would never
 * store or compare plaintext passwords like this - it would hash
 * passwords and check them against a proper user store.</p>
 */
public class AdminAuthService {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    /**
     * Checks the supplied credentials against the admin account.
     *
     * @return true if both username and password match exactly
     */
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return ADMIN_USERNAME.equals(username.trim()) && ADMIN_PASSWORD.equals(password);
    }
}
