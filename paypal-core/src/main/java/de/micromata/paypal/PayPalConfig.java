package de.micromata.paypal;

import de.micromata.paypal.data.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PayPalConfig {
    private static Logger log = LoggerFactory.getLogger(PayPalConfig.class);

    public enum Mode {LIVE, SANDBOX}

    static final String KEY_MODE = "paypal.mode";
    static final String KEY_CLIENT_ID = "paypal.client_id";
    static final String KEY_SECRET = "paypal.secret";
    static final String KEY_RETURN_URL = "paypal.return_url";
    static final String KEY_CANCEL_URL = "paypal.cancel_url";
    static final String KEY_NO_WARRANTY_ACCEPTANCE = "paypal.no_warranty_acceptance";
    public static final String DEMO_RETURN_URL = "https://example.com/your_redirect_url.html";
    public static final String DEMO_CANCEL_URL = "https://example.com/your_cancel_url.html";

    private String clientId;
    private String clientSecret;
    private String returnUrl;
    private String cancelUrl;
    private String defaultPayment = "paypal";
    private Mode mode = Mode.SANDBOX;
    private String noWarrantyAcceptance;

    public PayPalConfig() {
    }

    public PayPalConfig read(File propertiesFile) throws IOException {
        log.info("Loading properties from file '" + propertiesFile.getAbsolutePath() + "'.");
        Properties props = new Properties();
        props.load(new FileReader(propertiesFile));
        return read(props);
    }

    public PayPalConfig read(Properties props) {
        String mode = props.getProperty(KEY_MODE);
        if ("live".equals(mode)) {
            this.mode = Mode.LIVE;
        } else {
            this.mode = Mode.SANDBOX;
        }
        clientId = props.getProperty(KEY_CLIENT_ID);
        clientSecret = props.getProperty(KEY_SECRET);
        returnUrl = props.getProperty(KEY_RETURN_URL);
        cancelUrl = props.getProperty(KEY_CANCEL_URL);
        noWarrantyAcceptance = props.getProperty(KEY_NO_WARRANTY_ACCEPTANCE);
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId
     * @return this for chaining.
     */
    public PayPalConfig setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        checkNowWarranty();
        return clientSecret;
    }

    public PayPalConfig setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    /**
     * This default return url is used if not explicitly set.
     *
     * @return The default return url used in {@link PayPalConnector#createPayment(PayPalConfig, Payment)}.
     */
    public String getReturnUrl() {
        return returnUrl;
    }

    public PayPalConfig setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
        return this;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    /**
     * This default cancel url is used if not explicitly set.
     *
     * @return The default cancel url used in {@link PayPalConnector#createPayment(PayPalConfig, Payment)}.
     */
    public PayPalConfig setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
        return this;
    }

    /**
     * The payment used as default if no other is set.
     *
     * @return "paypal" as default.
     */

    public String getDefaultPayment() {
        return defaultPayment;
    }

    public PayPalConfig setDefaultPayment(String defaultPayment) {
        this.defaultPayment = defaultPayment;
        return this;
    }

    public Mode getMode() {
        return mode;
    }

    public PayPalConfig setMode(Mode mode) {
        this.mode = mode;
        if (this.mode == Mode.LIVE) {
            log.info("******** PLEASE TAKE CARE ON USING YOUR SOFTWARE AGAINST PAYPAL'S LIVE SYSTEM *******");
        }
        return this;
    }

    public static PayPalConfig createDemoConfig() {
        PayPalConfig config = new PayPalConfig();
        config.setReturnUrl(DEMO_RETURN_URL);
        config.setCancelUrl(DEMO_CANCEL_URL);
        return config;
    }

    public void setNoWarrantyAcceptance(String noWarrantyAcceptance) {
        this.noWarrantyAcceptance = noWarrantyAcceptance;
    }

    private void checkNowWarranty() {
        if (this.mode == Mode.LIVE &&
                !I_CONFIRM_THAT_I_AM_USING_THIS_SOFTWARE_ON_MY_OWN_RISK_IT_IS_AN_OPEN_SOURCE_DEMO_SOFTWARE_AS_AN_EXAMPLE_ON_HOW_TO_USE_A_PAYPAL_INTEGRATION_FOR_USAGE_I_HAVE_TO_TEST_AND_MODIFY_THIS_SOFTWARE_THERE_IS_NO_WARRANTY_GIVEN_BY_THE_DEVELOPER_OR_ASSOCIATED_COMPANIES
                        .equals(this.noWarrantyAcceptance)) {
            String msg = "Sorry Dude, you have to accept, that you have to use this open source software on your own risk. Refer the log files for more details.";
            log.error("**** " + msg);
            log.error("Please configure property '" + KEY_NO_WARRANTY_ACCEPTANCE + "' in the config file or set it directly in PayPalConfig.setNoWarrantyAcceptance(...) to the following value:");
            log.error("No warranty value = '"
                    + I_CONFIRM_THAT_I_AM_USING_THIS_SOFTWARE_ON_MY_OWN_RISK_IT_IS_AN_OPEN_SOURCE_DEMO_SOFTWARE_AS_AN_EXAMPLE_ON_HOW_TO_USE_A_PAYPAL_INTEGRATION_FOR_USAGE_I_HAVE_TO_TEST_AND_MODIFY_THIS_SOFTWARE_THERE_IS_NO_WARRANTY_GIVEN_BY_THE_DEVELOPER_OR_ASSOCIATED_COMPANIES
                    + "'");
            throw new IllegalArgumentException(msg);
        }
    }

    public static final String I_CONFIRM_THAT_I_AM_USING_THIS_SOFTWARE_ON_MY_OWN_RISK_IT_IS_AN_OPEN_SOURCE_DEMO_SOFTWARE_AS_AN_EXAMPLE_ON_HOW_TO_USE_A_PAYPAL_INTEGRATION_FOR_USAGE_I_HAVE_TO_TEST_AND_MODIFY_THIS_SOFTWARE_THERE_IS_NO_WARRANTY_GIVEN_BY_THE_DEVELOPER_OR_ASSOCIATED_COMPANIES
            = "I CONFIRM THAT I AM USING THIS SOFTWARE ON MY OWN RISK. "
            + "IT IS AN OPEN SOURCE DEMO SOFTWARE AS AN EXAMPLE ON HOW TO USE A PAYPAL INTEGRATION. FOR USAGE I HAVE "
            + "TO TEST AND MODIFY THIS SOFTWARE. THERE IS NO WARRANTY GIVEN BY THE DEVELOPER OR ASSOCIATED COMPANIES.";
}
