package de.micromata.paypal.data;

import java.util.List;

/**
 * List of payments as response of call <tt>/v1/payments/payment</tt>
 */
public class Payments {
    private List<Payment> payments;
    private int count;
    private String nextId;

    /**
     * @return
     * an array of payments that are complete. Payments that you just created with the create payment call do not appear in the list.
     */
    public List<Payment> getPayments() {
        return payments;
    }

    /**
     * @return
     * the number of items returned in each range of results. Note that the last results range might have fewer items than the requested number of items.
     * Maximum value: 20.
     */
    public int getCount() {
        return count;
    }

    /**
     * @return the ID of the element to use to get the next range of results.<br>
     * Read only.
     */
    public String getNextId() {
        return nextId;
    }
}
