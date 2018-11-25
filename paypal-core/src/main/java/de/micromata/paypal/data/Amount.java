package de.micromata.paypal.data;

import java.math.BigDecimal;

public class Amount {
    private String currency;
    private Details details = new Details();

    public Amount() {
        currency = Currency.USD.name();
    }

    public Amount(String currency) {
        this.currency = currency;
    }

    public Amount(Currency currency) {
        setCurrency(currency);
    }

    /**
     * The details object is automatically created and should be calculated by
     * {@link Transaction#calculate()}
     * @return details
     */
    public Details getDetails() {
        return details;
    }

    public BigDecimal getTotal() {
        if (details == null) {
            return BigDecimal.ZERO;
        }
        return details.getTotal();
    }

    public String getCurrency() {
        return currency;
    }

    /**
     * Ensures 3 character value: https://developer.paypal.com/docs/api/payments/v1/#definition-amount
     * @param currency The currency as string (3 chars).
     */
    public void setCurrency(String currency) {
        if (currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a three-character ISO-4217 currency code.");
        }
        this.currency = currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency.name();
    }
}
