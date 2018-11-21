package de.micromata.paypal.data;

import de.micromata.paypal.Utils;

import java.beans.Transient;
import java.math.BigDecimal;

/**
 * Details of Amount.
 */
public class Details {
    private BigDecimal shipping;
    private BigDecimal tax;
    private BigDecimal subtotal;

    @Transient
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        total = Utils.add(total, shipping, tax, subtotal);
        return Utils.roundAmount(total);
    }


    public BigDecimal getShipping() {
        return shipping;
    }

    /**
     * Ensures scale 2.
     *
     * @param shipping
     * @return this for chaining.
     */
    public Details setShipping(BigDecimal shipping) {
        this.shipping = Utils.roundAmount(shipping);
        return this;
    }

    public Details setShipping(double shipping) {
        return setShipping(new BigDecimal(shipping));
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    /**
     * Calculate subtotal and tax by adding all prices (prices are multiplied with quantity) and taxes of the items of
     * the transaction this details will assigned to.
     * @param transaction
     */
    public void calculate(Transaction transaction) {
        subtotal = BigDecimal.ZERO;
        for (Item item : transaction.getItemList().getItems()) {
            subtotal = Utils.add(subtotal, item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        tax = BigDecimal.ZERO;
        for (Item item : transaction.getItemList().getItems()) {
            tax = Utils.add(tax, item.getTax());
        }
    }
}
