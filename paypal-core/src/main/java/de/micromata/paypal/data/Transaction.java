package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.paypal.Utils;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.List;

public class Transaction {
    private Amount amount;
    private String inoviceNumber;
    private String description;
    private Payee payee;
    private ItemList itemList = new ItemList();
    private List<RelatedResource> relatedResources;

    /**
     * Needed for json deserialization.
     */
    Transaction() {
    }

    /**
     * Ensures all items with the same currency.
     * @param currency Each transaction needs a currency.
     */
    public Transaction(Currency currency) {
        amount = new Amount(currency);
    }


    /**
     * Ensures all items with the same currency. Use this constructor if your currency is not listed in the
     * enum {@link Currency}.
     * @param currency Each transaction needs a currency.
     */
    public Transaction(String currency) {
        amount = new Amount(currency);
    }

    public Item addItem(String name, BigDecimal price) {
        Item item = new Item().setPrice(price).setName(name);
        itemList.add(item);
        return item;
    }

    public Item addItem(String name, double price) {
        Item item = new Item().setPrice(price).setName(name);
        itemList.add(item);
        return item;
    }

    public Amount getAmount() {
        return amount;
    }

    /**
     *
     * @param amount amount to set.
     * @return this for chaining.
     */
    public Transaction setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    @JsonProperty(value = "invoice_number")
    public String getInoviceNumber() {
        return inoviceNumber;
    }

    /**
     * Ensures maximum length of 127: https://developer.paypal.com/docs/api/payments/v1/#definition-transaction
     *
     * @param inoviceNumber invoice number
     * @return this for chaining.
     */
    public Transaction setInoviceNumber(String inoviceNumber) {
        this.inoviceNumber = Utils.ensureMaxLength(inoviceNumber, 127);
        return this;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Ensures maximum length of 127: https://developer.paypal.com/docs/api/payments/v1/#definition-transaction
     *
     * @param description description
     * @return this for chaining.
     */
    public Transaction setDescription(String description) {
        this.description = Utils.ensureMaxLength(description, 127);
        return this;
    }

    @JsonProperty("item_list")
    public ItemList getItemList() {
        return itemList;
    }

    @Transient
    public List<Item> getItems() {
        return itemList.getItems();
    }

    /**
     * @param itemList item list
     * @return this for chaining.
     */
    public Transaction setItemList(ItemList itemList) {
        this.itemList = itemList;
        return this;
    }

    public Payee getPayee() {
        return payee;
    }

    @JsonProperty(value = "related_resources")
    public List<RelatedResource> getRelatedResources() {
        return relatedResources;
    }

    /**
     * Calls {@link Details#calculate(Transaction)} with this transaction and sets the currency
     * of all containes items (uses the amount's currency).
     */
    public void calculate() {
        amount.getDetails().calculate(this);
        String currency = amount.getCurrency();
        for (Item item : itemList.getItems()) {
            item.setCurrency(currency);
        }

    }
}
