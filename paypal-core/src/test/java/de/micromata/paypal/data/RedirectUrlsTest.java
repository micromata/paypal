package de.micromata.paypal.data;

import de.micromata.paypal.PayPalConfig;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RedirectUrlsTest {
    private static Logger log = LoggerFactory.getLogger(RedirectUrlsTest.class);

    @Test
    void exampleUrlsDetectionTest() {
        PayPalConfig config = new PayPalConfig();
        log.info("A warning message should follow:");
        new RedirectUrls().setConfig(config);
        config.setReturnUrl("https://www.acme.com/myapp/paypal-return");
        log.info("Another warning message should follow:");
        new RedirectUrls().setConfig(config);
        config.setCancelUrl("https://www.acme.com/myapp/paypal-cancel");
        log.info("No warning message should follow.");
        new RedirectUrls().setConfig(config);

        config = new PayPalConfig();
        config.setMode(PayPalConfig.Mode.LIVE);
        try {
            new RedirectUrls().setConfig(config);
            fail("IllegalAccessException should be thrown due to missing return urls.");
        } catch (IllegalArgumentException ex) {
            // OK.
        }

        int expiresIn = 30539;
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setExpiresInNotUpdated(expiresIn);
        long now = System.currentTimeMillis() * 1000;
        assertTrue(accessTokenResponse.getExpiresIn() < expiresIn + 1);
        assertTrue(accessTokenResponse.getExpiresIn() > expiresIn - 10);
    }
}
