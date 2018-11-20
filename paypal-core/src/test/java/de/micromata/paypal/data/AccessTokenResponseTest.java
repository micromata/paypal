package de.micromata.paypal.data;

import de.micromata.paypal.json.JsonUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessTokenResponseTest {
    private static Logger log = LoggerFactory.getLogger(AccessTokenResponseTest.class);

    @Test
    void expiresInTest() {
        int expiresIn = 30539;
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setExpiresInNotUpdated(expiresIn);
        long now = System.currentTimeMillis() * 1000;
        assertTrue(accessTokenResponse.getExpiresIn() < expiresIn + 1);
        assertTrue(accessTokenResponse.getExpiresIn() > expiresIn - 10);
    }
}
