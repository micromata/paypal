package de.micromata.paypal;

/**
 * Only for demonstration.
 */
class UserFakeSession {
    String paymentId, payerId;

    UserFakeSession(String paymentId, String payerId) {
        this.paymentId = paymentId;
        this.payerId = payerId;
    }
}
