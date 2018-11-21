package de.micromata.paypal;

import de.micromata.paypal.data.*;
import de.micromata.paypal.json.JsonUtils;

import java.io.File;

public class PayPalConnectorTester {

    public static void main(String[] args) throws Exception {
        File file = new File(System.getProperty("user.home"), ".paypal");
        PayPalConfig config = new PayPalConfig().read(file);
        getAccessToken(config);
        createPayment(config);
    }

    private static void getAccessToken(PayPalConfig config) throws Exception {
        System.out.println(JsonUtils.toJson(PayPalConnector.getAccessToken(config), true));
    }

    private static void createPayment(PayPalConfig config) throws Exception {
        Payment payment = new Payment().setConfig(config);
        Transaction transaction = new Transaction(Currency.EUR);
        transaction.addItem("Online Elections 2019", 29.99).setTax(5.70);
        transaction.setInoviceNumber("1234");
        payment.addTransaction(transaction).setNoteToPayer("Enjoy your Elections with POLYAS.");
        //System.out.println(JsonUtils.toJson(payment, true));
        PaymentCreated paymentExecution = PayPalConnector.createPayment(config, payment);
    }
}