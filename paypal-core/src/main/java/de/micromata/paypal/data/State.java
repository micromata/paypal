package de.micromata.paypal.data;

import de.micromata.paypal.PayPalConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum State {
    CREATED("created"), APPROVED("approved"), FAILED("failed");

    private static Logger log = LoggerFactory.getLogger(State.class);

    private String str;

    public static State getState(String str) {
        if (str == null) {
            return null;
        }
        if (CREATED.str.equals(str))
            return State.CREATED;
        else if (APPROVED.str.equals(str))
            return State.APPROVED;
        else if (FAILED.str.equals(str))
            return State.FAILED;
        log.error("State string '" + str + "' not supported.");
        return null;
    }

    State(String str) {
        this.str = str;
    }

    public String getAsString() {
        return str;
    }
}
