package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.paypal.PayPalConfig;
import de.micromata.paypal.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedirectUrls {
    private static Logger log = LoggerFactory.getLogger(RedirectUrls.class);
    private String returnUrl, cancelUrl;

    /**
     * Sets the return an cancel ur, if not already set. If one of the values is already set the set
     * value will not be overwritten.
     *
     * @param config
     * @return
     */
    public RedirectUrls setConfig(PayPalConfig config) {
        boolean warningShown = false;
        if (Utils.isBlank(this.returnUrl)) {
            if (Utils.isBlank(config.getReturnUrl()) ||
                    PayPalConfig.DEMO_RETURN_URL.equals(config.getReturnUrl())) {
                if (config.getMode() == PayPalConfig.Mode.LIVE) {
                    throw new IllegalArgumentException("Don't use the example return urls for live PayPal. Please configure the return urls.");
                }
                warningShown = true;
                log.warn("You use the example return url(s). It's only OK for testing...");
            }
            this.returnUrl = config.getReturnUrl();
        }
        if (Utils.isBlank(this.cancelUrl)) {
            if (Utils.isBlank(config.getCancelUrl()) ||
                    PayPalConfig.DEMO_CANCEL_URL.equals(config.getCancelUrl())) {
                if (config.getMode() == PayPalConfig.Mode.LIVE) {
                    throw new IllegalArgumentException("Don't use the example return urls for live PayPal. Please configure the return urls.");
                }
                if (!warningShown)
                    log.warn("You use the example return url(s). It's only OK for testing...");
            }
            this.cancelUrl = config.getCancelUrl();
        }
        return this;
    }

    @JsonProperty(value = "return_url")
    public String getReturnUrl() {
        return returnUrl;
    }

    /**
     * You may overwrite the default return url of {@link PayPalConfig}.
     *
     * @param returnUrl
     * @return this for chaining.
     */
    public RedirectUrls setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
        return this;
    }

    @JsonProperty(value = "cancel_url")
    public String getCancelUrl() {
        return cancelUrl;
    }

    /**
     * You may overwrite the default cancel url of {@link PayPalConfig}.
     *
     * @param cancelUrl
     * @return this for chaining.
     */
    public RedirectUrls setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
        return this;
    }
}
