package de.micromata.paypal;

import de.micromata.paypal.data.*;
import de.micromata.paypal.json.JsonUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentTest {
    private static Logger log = LoggerFactory.getLogger(PaymentTest.class);

    @Test
    void paymentCreatedSerializationTest() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("test-data", "payment-created.json")));
        Payment payment = JsonUtils.fromJson(Payment.class, content);
        assertEquals("PAY-1XN596375S038633ELP4TDNQ", payment.getId());
        assertEquals(State.CREATED, payment.getState());
        Transaction transaction = payment.getTransactions().get(0);
        Amount amount = transaction.getAmount();
        assertEquals("40.87", amount.getTotal().toString());
        assertEquals(Currency.EUR.name(), amount.getCurrency());
        Details details = amount.getDetails();
        assertEquals("30.99", details.getSubtotal().toString());
        assertEquals("5.89", details.getTax().toString());
        assertEquals("3.99", details.getShipping().toString());
        assertEquals("Enjoy your Elections with POLYAS.", payment.getNoteToPayer());
        assertEquals("2018-11-24T11:10:45Z", payment.getCreateTime());

        assertEquals(2, transaction.getItems().size());
        testItem(transaction.getItems().get(0), "Online Elections 2019", "29.99", Currency.EUR, "5.70", 1);
        testItem(transaction.getItems().get(1), "Logo", "1.00", Currency.EUR, "0.19", 1);

        assertEquals("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-0LG63851YH496714A", payment.getPayPalApprovalUrl());
    }

    @Test
    void paymentApprovedSerializationTest() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("test-data", "payment-approved.json")));
        Payment payment = JsonUtils.fromJson(Payment.class, content);
        assertEquals("PAY-6MT6019334734810GLP2QJOI", payment.getId());
        assertEquals(State.APPROVED, payment.getState());
        Transaction transaction = payment.getTransactions().get(0);
        Amount amount = transaction.getAmount();
        assertEquals("29.99", amount.getTotal().toString());
        assertEquals(Currency.EUR.name(), amount.getCurrency());
        assertEquals("Merlin Online Solutions", transaction.getDescription());
        Details details = amount.getDetails();
        assertEquals("29.99", details.getSubtotal().toString());
        assertEquals("0.00", details.getTax().toString());
        assertNull(details.getShipping());
        assertNull(payment.getNoteToPayer());
        assertEquals("2018-11-21T07:10:04Z", payment.getCreateTime());

        assertEquals(1, transaction.getItems().size());
        testItem(transaction.getItems().get(0), "Elections 2019", "29.99", Currency.EUR, "0.00", 1);

        assertEquals("7H166117V65459919", payment.getCart());
        Payer payer = payment.getPayer();
        assertEquals("VERIFIED", payer.getStatus());
        assertEquals("paypal", payer.getPaymentMethod());
        PayerInfo payerInfo = payer.getPayerInfo();
        assertEquals("test-buyer@acme.com", payerInfo.getEmail());
        assertEquals("test", payerInfo.getFirstName());
        assertEquals("buyer", payerInfo.getLastName());
        assertEquals("9KV4FWVA79N94", payerInfo.getPayerId());
        assertEquals("DE", payerInfo.getCountryCode());

        Payee payee = transaction.getPayee();
        assertEquals("admin-facilitator@acme.de", payee.getEmail());
        assertEquals("LKHIUL76E", payee.getMerchantId());
    }

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

    private void testItem(Item item, String name, String price, Currency currency, String tax, int quantity) {
        assertEquals(name, item.getName());
        assertEquals(price, item.getPrice().toString());
        assertEquals(currency.name(), item.getCurrency());
        assertEquals(tax, item.getTax().toString());
        assertEquals(quantity, item.getQuantity());
    }
}