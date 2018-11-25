package de.micromata.paypal.data;

import de.micromata.paypal.Utils;

import java.math.BigDecimal;

public class Item {
    private String name;
    private int quantity = 1;
    private BigDecimal price;
    private BigDecimal tax;
    private String currency;

    public String getName() {
        return name;
    }

    /**
     * Ensures maximum length of 127: https://developer.paypal.com/docs/api/payments/v1/#definition-item
     * @param name name of item
     * @return this for chaining.
     */
    public Item setName(String name) {
        this.name = Utils.ensureMaxLength(name, 127);
        return this;
    }

    /**
     * Defaut is 1.
     *
     * @return quantity of this item
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     *
     * @param quantity quantity of this item
     * @return this for chaining.
     * @throws IllegalArgumentException if the given quantity is zero or negative.
     */
    public Item setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity of item can't be zero or negative.");
        }
        this.quantity = quantity;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Set net price and ensures a BigDecimal value with scale 2.
     * @param price Net price.
     * @return this for chaining.
     */
    public Item setPrice(BigDecimal price) {
        this.price = Utils.roundAmount(price);
        return this;
    }

    /**
     *
     * @param price Net price
     * @return this for chaining.
     * Calls {@link #setPrice(BigDecimal)}
     */
    public Item setPrice(double price) {
        return setPrice(new BigDecimal(price));
    }

    public BigDecimal getTax() {
        return tax;
    }

    /**
     * Additional tax to the net price.
     * @param tax additional tax amount
     * @return this for chaining.
     * Calls {@link #addTax(BigDecimal)}
     */
    public Item setTax(BigDecimal tax) {
        return addTax(tax);
    }

    /**
     * Ensures a BigDecimal value with scale 2.
     * @param tax additional tax amount
     * @return this for chaining.
     */
    public Item addTax(BigDecimal tax) {
        this.tax = Utils.roundAmount(tax);
        return this;
    }

    /**
     * Additional tax to the net price.
     * @param tax additional tax amount
     * @return this for chaining.
     * Calls {@link #addTax(double)}
     */
    public Item setTax(double tax) {

        return addTax(tax);
    }

    /**
     * Adds tax amount.
     * @param tax additional tax amount
     * @return this for chaining.
     * Calls {@link #addTax(BigDecimal)}
     */
    public Item addTax(double tax) {
        return addTax(new BigDecimal(tax));
    }

    public String getCurrency() {
        return currency;
    }

    void setCurrency(String currency) {
        this.currency = currency;
    }
}
