package de.micromata.paypal;

import de.micromata.paypal.data.*;
import de.micromata.paypal.json.JsonUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PayPalConnectorTest {
    private Logger log = LoggerFactory.getLogger(PayPalConnectorTest.class);

    private PayPalConfig getConfig() throws IOException {
        File file = new File(System.getProperty("user.home"), ".paypal");
        if (!file.exists()) {
            log.info("No paypal configured, aborting.");
            return null;
        }
        return new PayPalConfig().read(file);
    }

    @Test
    void accessTokenTest() throws IOException, PayPalRestException {
        PayPalConfig config = getConfig();
        if (config == null) {
            return;
        }
        getAccessToken(config);
    }

    @Test
    void createPaymentTest() throws IOException, PayPalRestException {
        PayPalConfig config = getConfig();
        if (config == null) {
            return;
        }
        createPayment(config);
    }

    @Test
    void getPaymentsTest() throws IOException, PayPalRestException {
        PayPalConfig config = getConfig();
        if (config == null) {
            return;
        }
        PaymentRequestFilter filter = new PaymentRequestFilter();
        Payments payments = PayPalConnector.listPayments(config, filter);
        if (payments.getPayments() != null && payments.getPayments().size() > 0) {
            Payment payment1 = payments.getPayments().get(0);
            String paymentId = payment1.getId();
            Payment payment2 = PayPalConnector.getPaymentDetails(config, paymentId);
            assertEquals(payment1.getId(), payment2.getId());
        }
    }

    private void getAccessToken(PayPalConfig config) throws PayPalRestException {
        log.info(JsonUtils.toJson(PayPalConnector.getAccessToken(config), true));
    }

    private void createPayment(PayPalConfig config) throws PayPalRestException {
        Transaction transaction = new Transaction(Currency.EUR);
        transaction.addItem("Online Elections 2019", 29.99).setTax(5.70);
        transaction.addItem("Logo", 1.00).setTax(0.19);
        transaction.getAmount().getDetails().setShipping(3.99);
        String invoiceNumber = "I-" + new Random().nextInt();
        transaction.setInoviceNumber(invoiceNumber);
        Payment payment = new Payment(transaction).setNoteToPayer("Enjoy your Elections with POLYAS.");
        //log.info(JsonUtils.toJson(payment, true));
        Payment paymentCreated = PayPalConnector.createPayment(config, payment);
        assertEquals(1, paymentCreated.getTransactions().size());
        transaction = paymentCreated.getTransactions().get(0);
        assertEquals(invoiceNumber, transaction.getInoviceNumber());

        assertEquals("40.87", transaction.getAmount().getTotal().toString());
        assertEquals("30.99", transaction.getAmount().getDetails().getSubtotal().toString());
        assertEquals("5.89", transaction.getAmount().getDetails().getTax().toString());

        assertEquals(2, transaction.getItems().size());
        testItem(transaction.getItems().get(0), "Online Elections 2019", "29.99", 1, "5.70");
        testItem(transaction.getItems().get(1), "Logo", "1.00", 1, "0.19");
    }

    private static void testItem(Item item, String name, String price, int quantity, String tax) {
        assertEquals(name, item.getName());
        assertEquals(price, item.getPrice().toString());
        assertEquals(quantity, item.getQuantity());
        assertEquals(tax, item.getTax().toString());
    }
}