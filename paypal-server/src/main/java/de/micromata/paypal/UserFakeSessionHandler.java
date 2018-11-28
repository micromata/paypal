package de.micromata.paypal;

import java.util.HashMap;
import java.util.Map;

/**
 * Only for demonstration.
 */
class UserFakeSessionHandler {
    private static final UserFakeSessionHandler INSTANCE = new UserFakeSessionHandler();
    private Map<String, UserFakeSession> sessions = new HashMap<>();

    static UserFakeSessionHandler getInstance() {
        return INSTANCE;
    }

    UserFakeSession getSession(String ip) {
        return sessions.get(ip);
    }

    void store(String ip, UserFakeSession session) {
        sessions.put(ip, session);
    }

    void clear(String ip) {
        sessions.remove(ip);
    }
}
