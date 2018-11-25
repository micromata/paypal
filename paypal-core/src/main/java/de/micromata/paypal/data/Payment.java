package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.paypal.PayPalConfig;
import de.micromata.paypal.PayPalConnector;
import de.micromata.paypal.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Use this class to prepare your payment. This payment will be sent to PayPal:
 * {@link PayPalConnector#createPayment(PayPalConfig, Payment)}
 */
public class Payment {
    private String intent = "sale";
    private String id, stateString, cart, createTime, updateTime, failureReason, experienceProfileId;
    private Payer payer = new Payer();
    private List<Transaction> transactions = new ArrayList<>();
    private String noteToPayer;
    private ApplicationContext applicationContext;
    private RedirectUrls redirectUrls = new RedirectUrls();
    private List<Link> links;
    private String origninalPayPalResponse;

    public Payment() {
    }

    /**
     * Creates a payment with this transaction. You may add more transactions by calling {@link #addTransaction(Transaction)}.
     *
     * @param transaction transaction to add.
     */
    public Payment(Transaction transaction) {
        addTransaction(transaction);
    }

    /**
     * @return The ID of the payment given by PayPal.
     */
    public String getId() {
        return id;
    }

    /**
     * The state of the payment, authorization, or order transaction. Value is:
     * <ul>
     * <li>created. The transaction was successfully created.</li>
     * <li>approved. The customer approved the transaction. The state changes from created to approved on generation of the sale_id for sale transactions, authorization_id for authorization transactions, or order_id for order transactions.</li>
     * <li>failed. The transaction request failed.</li>
     * </ul>
     * Read only.
     * <p>
     * Possible values: created, approved, failed.
     *
     * @return State given by PayPal
     */
    @JsonProperty(value = "state")
    public String getStateString() {
        return stateString;
    }

    public State getState() {
        return State.getState(stateString);
    }

    public String getCart() {
        return cart;
    }

    @JsonProperty(value = "create_time")
    public String getCreateTime() {
        return createTime;
    }

    @JsonProperty(value = "update_time")
    public String getUpdateTime() {
        return updateTime;
    }

    public List<Link> getLinks() {
        return links;
    }

    /**
     * Default is "sale".
     * @return intent of payment.
     */
    public String getIntent() {
        return intent;
    }

    public Payer getPayer() {
        return payer;
    }

    public Payment addTransaction(Transaction transaction) {
        transactions.add(transaction);
        return this;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @JsonProperty(value = "note_to_payer")
    public String getNoteToPayer() {
        return noteToPayer;
    }

    /**
     * Ensures max length 165: https://developer.paypal.com/docs/api/payments/v1/#definition-transaction
     *
     * @param noteToPayer Important not to the payer shown on the PayPal site.
     * @return this for chaining.
     */
    public Payment setNoteToPayer(String noteToPayer) {
        this.noteToPayer = Utils.ensureMaxLength(noteToPayer, 165);
        return this;
    }

    @JsonProperty(value = "redirect_urls")
    public RedirectUrls getRedirectUrls() {
        return redirectUrls;
    }

    /**
     * This method is automatically called by {@link PayPalConnector#createPayment(PayPalConfig, Payment)} and
     * adds the return urls for PayPal.
     *
     * @param config config needed for setting redirectUrls if not already set.
     * @return this for chaining.
     * @see RedirectUrls#setConfig(PayPalConfig)
     */
    public Payment setConfig(PayPalConfig config) {
        redirectUrls.setConfig(config);
        return this;
    }

    /**
     * Is called internally before processing a payment for updating item currencies and calculating sums etc.
     */
    public void recalculate() {
        for (Transaction transaction : transactions) {
            transaction.calculate();
        }
    }

    @JsonProperty(value = "application_context")
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Payment setShipping(ShippingPreference shippingPreference) {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        }
        applicationContext.setShippingPreference(shippingPreference);
        return this;
    }

    /**
     * @return the original response from PayPal. This object is generated from this json string.
     */
    public String getOrigninalPayPalResponse() {
        return origninalPayPalResponse;
    }

    public void setOrigninalPayPalResponse(String origninalPayPalResponse) {
        this.origninalPayPalResponse = origninalPayPalResponse;
    }

    /**
     * After creating a payment on the PayPal servers, PayPal provides a url to redirect the user to for doing the
     * payment.
     * @return Redirect href provided by PayPal for the user to proceed with the payment.
     */
    public String getPayPalApprovalUrl() {
        if (links == null) {
            return null;
        }
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                // Redirect the customer to link.getHref()
                return link.getHref();
            }
        }
        return null;
    }

    @JsonProperty(value = "failure_reason")
    public String getFailureReason() {
        return failureReason;
    }

    @JsonProperty(value = "experience_profile_id")
    public String getExperienceProfileId() {
        return experienceProfileId;
    }
}
