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

class PaymentTest {
    private static Logger log = LoggerFactory.getLogger(PaymentTest.class);

    @Test
    void serializationTest() throws IOException {
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

        testItem(transaction.getItems().get(0), "Online Elections 2019", "29.99", Currency.EUR,"5.70", 1);
        testItem(transaction.getItems().get(1), "Logo", "1.00", Currency.EUR,"0.19", 1);
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