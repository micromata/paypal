package de.micromata.paypal;

import de.micromata.paypal.data.AccessTokenResponse;
import de.micromata.paypal.data.Payment;
import de.micromata.paypal.data.Payments;
import de.micromata.paypal.http.HttpsClient;
import de.micromata.paypal.http.MimeType;
import de.micromata.paypal.http.QueryParamBuilder;
import de.micromata.paypal.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Provides PayPal calls and transforms requests and responses to Java POJO's.
 */
public class PayPalConnector {
    private static Logger log = LoggerFactory.getLogger(PayPalConnector.class);

    /**
     * Create a payment in PayPal and get the information from PayPal including the redirect url for th user.
     *
     * @param config config contains the PayPal credentials and return urls.
     * @param payment payment to publish / create.
     * @return the created payment understood, processed and returned by PayPal.
     * @throws PayPalRestException Will be thrown if any exception occurs.
     */
    public static Payment createPayment(PayPalConfig config, Payment payment) throws PayPalRestException {
        try {
            // Post
            String url = getUrl(config, "/v1/payments/payment");
            payment.setConfig(config);
            payment.recalculate();
            log.info("Create payment: " + JsonUtils.toJson(payment));
            String response = doPostCall(config, url, JsonUtils.toJson(payment));
            Payment paymentCreated = JsonUtils.fromJson(Payment.class, response);
            if (paymentCreated == null) {
                throw new PayPalRestException("Error while creating payment: " + response);
            }
            paymentCreated.setOriginalPayPalResponse(response);
            log.info("Created execution payment: " + JsonUtils.toJson(paymentCreated));
            return paymentCreated;
        } catch (Exception ex) {
            throw new PayPalRestException("Error while creating payment: " + ex.getMessage(), ex);
        }
    }

    /**
     * @param config config contains the required PayPal credentials.
     * @param paymentId id of the payment to query.
     * @return The payment returned by PayPal, if found.
     * @throws PayPalRestException Will be thrown if any exception occurs.
     */
    public static Payment getPaymentDetails(PayPalConfig config, String paymentId) throws PayPalRestException {
        try {
            // GET request
            // /v1/payments/payment/{payment_id}
            String url = getUrl(config, "/v1/payments/payment/" + paymentId);
            log.info("Get payment with paymentId=" + paymentId);
            String response = doGetCall(config, url);
            Payment payment = JsonUtils.fromJson(Payment.class, response);
            return payment;
        } catch (Exception ex) {
            throw new PayPalRestException("Error while getting payment details: " + ex.getMessage(), ex);
        }
    }

    /**
     * @param config config with PayPal credentials.
     * @param filter Query filter (can be empty or null).
     * @return All the payments found and returned by PayPal.
     * @throws PayPalRestException Will be thrown if any exception occurs.
     */
    public static Payments listPayments(PayPalConfig config, PaymentRequestFilter filter) throws PayPalRestException {
        try {
            // GET request
            // /v1/payments/payment
            String url = getUrl(config, "/v1/payments/payment");
            log.info("Get payments: filter=" + JsonUtils.toJson(filter));
            QueryParamBuilder pb = new QueryParamBuilder();
            if (filter != null) {
                pb.add("count", filter.getCount())
                        .add("start_id", filter.getStartId())
                        .add("start_index", filter.getStartIndex())
                        .add("start_time", filter.getStartTime())
                        .add("end_time", filter.getEndTime())
                        .add("payee_id", filter.getPayeeId())
                        .add("sort_by", filter.getSortBy())
                        .add("sort_order", filter.getSortOrder());
            }
            String response = doGetCall(config, pb.createUrl(url));
            if (log.isDebugEnabled()) log.debug("Response: " + response);
            Payments payments = JsonUtils.fromJson(Payments.class, response);
            return payments;
        } catch (Exception ex) {
            throw new PayPalRestException("Error while getting list of payments: " + ex.getMessage(), ex);
        }
    }

    /**
     * After finishing the payment by the user on the PayPal site, we have to execute this payment as a last step.
     *
     * @param config needed for PayPal credentials.
     * @param paymentId the payment id of the payment to execute.
     * @param payerId the id of the payer approved the payment.
     * @return The returned PaymentExecuted contain everything related to this payment, such as id, payer, refund urls etc.
     * @throws PayPalRestException Will be thrown if any exception occurs.
     */
    public static Payment executePayment(PayPalConfig config, String paymentId, String payerId) throws PayPalRestException {
        try {
            String url = getUrl(config, "/v1/payments/payment/" + paymentId + "/execute");
            log.info("Approve payment: paymentId=" + paymentId + ", payerId=" + payerId);
            String payload = "{\"payer_id\" : \"" + payerId + "\"}";
            String response = doPostCall(config, url, payload);
            if (log.isDebugEnabled()) log.debug("Response: " + response);
            Payment approval = JsonUtils.fromJson(Payment.class, response);
            if (approval == null) {
                throw new PayPalRestException("Error while creating payment: " + response);
            }
            approval.setOriginalPayPalResponse(response);
            log.info("Payment approved: " + JsonUtils.toJson(approval));
            return approval;
        } catch (Exception ex) {
            throw new PayPalRestException("Error while creating payment: " + ex.getMessage(), ex);
        }
    }

    /**
     * You may use the returned access token for doing PayPal calls inside your web pages.
     * <br>
     * Please, never ever use the PayPal credentials (client_id and secret) directly in your web pages.
     * <br>
     * The returned AccessToken is valid for e. g. 9h. During this time you will receive the same token from PayPal if you try to get
     * an access token again.
     * <br>
     * curl - v https:api.sandbox.paypal.com/v1/oauth2/token -H "Accept: application/json" -H "Accept-Language: en_US"
     * -u "&lt;client_id&gt;:&lt;secret&gt;" -d "grant_type=client_credentials"
     * <br>
     * Todo: You should refresh tokens x seconds before expiration:
     * https://developer.paypal.com/docs/integration/paypal-here/merchant-onboarding/permissions/#permissions-for-transaction-processing
     * @param config config with PayPal credentials.
     * @return AccessTokenResponse containing token and expire time.
     * @throws PayPalRestException Will be thrown if any exception occurs.
     */
    public static AccessTokenResponse getAccessToken(PayPalConfig config) throws PayPalRestException {
        try {
            String url = getUrl(config, "/v1/oauth2/token");
            HttpsClient httpsClient = new HttpsClient(url, HttpsClient.Mode.POST).setAcceptLanguage("en_US").setAccept(MimeType.JSON);
            httpsClient.setUserPasswordAuthorization(config.getClientId() + ":" + config.getClientSecret());
            String response = httpsClient.send("grant_type=client_credentials");
            AccessTokenResponse accessTokenResponse = JsonUtils.fromJson(AccessTokenResponse.class, response);
            accessTokenResponse.setOriginalPayPalResponse(response);
            return accessTokenResponse;
        } catch (Exception ex) {
            throw new PayPalRestException("Error while getting access token: " + ex.getMessage(), ex);
        }
    }


    private static String doPostCall(PayPalConfig config, String url, String body) throws IOException, MalformedURLException {
        return doPostCall(config, url, body, null);
    }

    private static String doPostCall(PayPalConfig config, String url, String body, String accessToken) throws IOException, MalformedURLException {
        HttpsClient httpsClient = createHttpsClient(config, accessToken, url, HttpsClient.Mode.POST);
        httpsClient.setContentType(MimeType.JSON);
        return httpsClient.send(body);
    }

    private static String doGetCall(PayPalConfig config, String url) throws IOException, MalformedURLException {
        return doGetCall(config, url, null);
    }

    private static String doGetCall(PayPalConfig config, String url, String accessToken) throws IOException, MalformedURLException {
        HttpsClient httpsClient = createHttpsClient(config, accessToken, url, HttpsClient.Mode.GET);
        return httpsClient.send();
    }

    private static HttpsClient createHttpsClient(PayPalConfig config, String accessToken, String url, HttpsClient.Mode mode) {
        HttpsClient httpsClient = new HttpsClient(url, mode).setAcceptLanguage("en_US").setAccept(MimeType.JSON);
        if (accessToken != null) {
            httpsClient.setBearerAuthorization(accessToken);
        } else {
            httpsClient.setUserPasswordAuthorization(config.getClientId() + ":" + config.getClientSecret());
        }
        return httpsClient;
    }

    private static String getUrl(PayPalConfig config, String url) {
        if (config.getMode() == PayPalConfig.Mode.SANDBOX) {
            return "https://api.sandbox.paypal.com" + url;
        } else {
            return "https://api.paypal.com" + url;
        }
    }
}
