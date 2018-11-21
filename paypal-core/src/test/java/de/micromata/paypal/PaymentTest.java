package de.micromata.paypal;

import de.micromata.paypal.data.*;
import de.micromata.paypal.json.JsonUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentTest {
    private static Logger log = LoggerFactory.getLogger(PaymentTest.class);

    @Test
    void paymentTest() {
        PayPalConfig config = PayPalConfig.createDemoConfig();
        Transaction transaction = new Transaction(Currency.EUR);
        transaction.setInoviceNumber(generateString(200));
        assertEquals(127, transaction.getInoviceNumber().length());
        assertEquals(generateString(124) + "...", transaction.getInoviceNumber());
        transaction.addItem("Online Elections 2019", 29.99).setTax(5.70);
        Payment payment = new Payment(transaction).setNoteToPayer("Enjoy your Elections with POLYAS.");
        payment.setConfig(config);
        payment.recalculate();
        assertEquals("35.69", transaction.getAmount().getTotal().toString());
        assertEquals("29.99", transaction.getAmount().getDetails().getSubtotal().toString());

        transaction.addItem("Logo", 1.00).setTax(.19);
        payment.recalculate();
        assertEquals("36.88", transaction.getAmount().getTotal().toString());
        assertEquals("30.99", transaction.getAmount().getDetails().getSubtotal().toString());
        assertEquals("5.89", transaction.getAmount().getDetails().getTax().toString());

        transaction.getAmount().getDetails().setShipping(3.99);
        payment.recalculate();
        assertEquals("40.87", transaction.getAmount().getTotal().toString());
        assertEquals("30.99", transaction.getAmount().getDetails().getSubtotal().toString());
        assertEquals("5.89", transaction.getAmount().getDetails().getTax().toString());
        log.info(JsonUtils.toJson(payment, true));
    }

    private String generateString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(i % 10);
        }
        return sb.toString();
    }
}