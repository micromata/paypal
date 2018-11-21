package de.micromata.paypal;

import de.micromata.paypal.data.Currency;
import de.micromata.paypal.data.Payment;
import de.micromata.paypal.data.Transaction;
import de.micromata.paypal.json.JsonUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class PayPalConfigTest {
    private static Logger log = LoggerFactory.getLogger(PayPalConfigTest.class);

    @Test
    void noWarrantyTest() {
        PayPalConfig config = new PayPalConfig().setClientSecret("secret");
        assertEquals("secret", config.getClientSecret());
        config.setMode(PayPalConfig.Mode.LIVE);
        try {
            config.getClientSecret();
            fail("Exception expected!");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().startsWith("Sorry Dude"));
        }
        config.setNoWarrantyAcceptance(PayPalConfig.I_CONFIRM_THAT_I_AM_USING_THIS_SOFTWARE_ON_MY_OWN_RISK_IT_IS_AN_OPEN_SOURCE_DEMO_SOFTWARE_AS_AN_EXAMPLE_ON_HOW_TO_USE_A_PAYPAL_INTEGRATION_FOR_USAGE_I_HAVE_TO_TEST_AND_MODIFY_THIS_SOFTWARE_THERE_IS_NO_WARRANTY_GIVEN_BY_THE_DEVELOPER_OR_ASSOCIATED_COMPANIES);
        assertEquals("secret", config.getClientSecret());
    }
}