package de.micromata.paypal;

import de.micromata.paypal.data.AccessTokenResponse;
import de.micromata.paypal.data.PaymentExecuted;
import de.micromata.paypal.data.PaymentCreated;
import de.micromata.paypal.data.Payment;
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
     * @param config
     * @param payment
     * @return the created payment understood, processed and returned by PayPal.
     * @throws PayPalRestException
     */
    public static PaymentCreated createPayment(PayPalConfig config, Payment payment) throws PayPalRestException {
        try {
            String url = getUrl(config, "/v1/payments/payment");
            payment.setConfig(config);
            payment.recalculate();
            log.info("Create payment: " + JsonUtils.toJson(payment));
            String response = executeCall(config, url, JsonUtils.toJson(payment));
            PaymentCreated executionPayment = JsonUtils.fromJson(PaymentCreated.class, response);
            if (executionPayment == null) {
                throw new PayPalRestException("Error while creating payment: " + response);
            }
            executionPayment.setOrigninalPayPalResponse(response);
            log.info("Created execution payment: " + JsonUtils.toJson(executionPayment));
            return executionPayment;
        } catch (Exception ex) {
            throw new PayPalRestException("Error while creating payment.", ex);
        }
    }

    /**
     * After finishing the payment by the user on the PayPal site, we have to execute this payment as a last step.
     *
     * @param config
     * @param payementId
     * @param payerId
     * @return The returned PaymentExecuted contain everything related to this payment, such as id, payer, refund urls etc.
     * @throws PayPalRestException
     */
    public static PaymentExecuted executePayment(PayPalConfig config, String payementId, String payerId) throws PayPalRestException {
        try {
            String url = getUrl(config, "/v1/payments/payment/" + payementId + "/execute");
            log.info("Approve payment: paymentId=" + payementId + ", payerId=" + payerId);
            String payload = "{\"payer_id\" : \"" + payerId + "\"}";
            String response = executeCall(config, url, payload);
            if (log.isDebugEnabled()) log.info("Response: " + response);
            PaymentExecuted approval = JsonUtils.fromJson(PaymentExecuted.class, response);
            if (approval == null) {
                throw new PayPalRestException("Error while creating payment: " + response);
            }
            approval.setOrigninalPayPalResponse(response);
            log.info("Payment approved: " + JsonUtils.toJson(approval));
            return approval;
        } catch (Exception ex) {
            throw new PayPalRestException("Error while creating payment.", ex);
        }
    }

    /**
     * You may use the returned access token for doing PayPal calls inside your web pages.
     * <br/>
     * Please, never ever use the PayPal credentials (client_id and secret) directly in your web pages.
     * <br/>
     * The returned AccessToken is valid for e. g. 9h. During this time you will receive the same token from PayPal if you try to get
     * an access token again.
     * <br/>
     * curl - v https:api.sandbox.paypal.com/v1/oauth2/token -H "Accept: application/json" -H "Accept-Language: en_US"
     * -u "<client_id>:<secret>" -d "grant_type=client_credentials"
     * <br/>
     * Todo: You should refresh tokens x seconds before expiration:
     * https://developer.paypal.com/docs/integration/paypal-here/merchant-onboarding/permissions/#permissions-for-transaction-processing
     */
    public static AccessTokenResponse getAccessToken(PayPalConfig config) throws PayPalRestException {
        try {
            String url = getUrl(config, "/v1/oauth2/token");
            HttpsCall call = new HttpsCall().setAcceptLanguage("en_US").setAccept(HttpsCall.MimeType.JSON);
            call.setUserPasswordAuthorization(config.getClientId() + ":" + config.getClientSecret());
            String response = call.post(url, "grant_type=client_credentials");
            AccessTokenResponse accessTokenResponse = JsonUtils.fromJson(AccessTokenResponse.class, response);
            accessTokenResponse.setOrigninalPayPalResponse(response);
            return accessTokenResponse;
        } catch (Exception ex) {
            throw new PayPalRestException("Error while creating payment.", ex);
        }
    }


    private static String executeCall(PayPalConfig config, String url, String payload) throws IOException, MalformedURLException {
        return executeCall(config, url, payload, null);
    }

    private static String executeCall(PayPalConfig config, String url, String payload, String accessToken) throws IOException, MalformedURLException {
        HttpsCall call = new HttpsCall().setAcceptLanguage("en_US").setAccept(HttpsCall.MimeType.JSON);
        if (accessToken != null) {
            call.setBearerAuthorization(accessToken);
        } else {
            call.setUserPasswordAuthorization(config.getClientId() + ":" + config.getClientSecret());
        }
        call.setContentType(HttpsCall.MimeType.JSON);
        return call.post(url, payload);
    }

    private static String getUrl(PayPalConfig config, String url) {
        if (config.getMode() == PayPalConfig.Mode.SANDBOX) {
            return "https://api.sandbox.paypal.com" + url;
        } else {
            return "https://api.paypal.com" + url;
        }
    }
}
