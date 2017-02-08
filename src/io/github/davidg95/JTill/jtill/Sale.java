/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class Sale implements Serializable {

    private int code;
    private List<SaleItem> saleItems;
    private BigDecimal total;
    private Customer customer;
    private Discount discount;
    private long time;
    private boolean chargeAccount;

    private SaleItem lastAdded;

    public Sale() {
        saleItems = new ArrayList<>();
        customer = null;
        total = new BigDecimal("0.00");
        chargeAccount = false;
    }

    public Sale(Customer c, boolean chargeAccount) {
        saleItems = new ArrayList<>();
        this.customer = c;
        this.chargeAccount = chargeAccount;
        total = new BigDecimal("0.00");
    }

    public Sale(int code, BigDecimal total, Customer customer, Discount discount, long time, boolean chargeAccount, List<SaleItem> saleItems) {
        this(code, total, customer, discount, time, chargeAccount);
        this.saleItems = saleItems;
    }

    public Sale(int code, BigDecimal total, Customer customer, Discount discount, long time, boolean chargeAccount) {
        this.code = code;
        this.total = total;
        this.customer = customer;
        this.discount = discount;
        this.time = time;
        this.chargeAccount = chargeAccount;
    }

    /**
     * This method adds products to the sale. First it will check if the product
     * has already been added. If it has been added then it will check if it is
     * open priced, if it is then it will check if the price is the same, if so
     * then the quantity is increased, if not then it continues checking the
     * products. If it is not open price but exists it will increase the
     * quantity. If it does not exist then it adds a new item.
     *
     * @param p
     * @param quantity
     * @return true if the item was already in the sale and is being re-added,
     * false if it is a new item in the sale.
     */
    public boolean addItem(Product p, int quantity) {
        //First check if the item has already been added
        for (SaleItem item : saleItems) {
            if (item.getProduct().getProductCode() == p.getProductCode()) {
                if (p.isOpen()) {
                    if (p.getPrice().compareTo(item.getProduct().getPrice()) == 0) {
                        BigDecimal inc = item.increaseQuantity(quantity);
                        this.total = total.add(inc);
                        this.lastAdded = new SaleItem(this, p, quantity);
                        return true; //The product is open price and the same price so increase the quantity and exit
                    } else {
                        continue; //The product is open but a different price so check the next item
                    }
                }
                //Product is not open price and does already exist
                BigDecimal inc = item.increaseQuantity(quantity);
                this.total = total.add(inc);
                this.lastAdded = new SaleItem(this, p, quantity);
                return true;
            }
        }
        //If the item is not already in the sale
        SaleItem item = new SaleItem(this, p, quantity);

        this.total = total.add(item.getPrice());
        saleItems.add(item);
        this.lastAdded = item;
        return false;
    }

    public void complete() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setProducts(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getLineCount() {
        return saleItems.size();
    }

    public int getTotalItemCount() {
        int count = 0;
        for (SaleItem item : saleItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public boolean isChargeAccount() {
        return chargeAccount;
    }

    public void setChargeAccount(boolean chargeAccount) {
        this.chargeAccount = chargeAccount;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
        for (SaleItem si : saleItems) {
            si.setPrice(si.getPrice().multiply(new BigDecimal(Double.toString((100 - discount.getPercentage()) / 100))));
        }
        updateTotal();
    }

    /**
     * Method to void an item from the sale. It will first check though the list
     * looking for the item. If the quantity of the item in the last is greater
     * than the quantity being voided, then the quantity of the item in the list
     * is simply reduced. If the quantities are the same then the item is
     * removed from the list.
     *
     * @param si
     */
    public void voidItem(SaleItem si) {
        for (SaleItem item : saleItems) {
            if (item.getProduct().getProductCode() == si.getProduct().getProductCode()) {
                if (item.getQuantity() > si.getQuantity()) { //If the quantities are different then reduce the quantity.
                    item.decreaseQuantity(si.getQuantity());
                    updateTotal();
                    return;
                } else if (item.getQuantity() == si.getQuantity()) { //If the quantities are the same then remove the item.
                    saleItems.remove(si);
                    updateTotal();
                    return;
                }
            }
        }
    }

    /**
     * Method to void an item from the sale. It will first check though the list
     * looking for the item. If the quantity of the item in the last is greater
     * than the quantity being voided, then the quantity of the item in the list
     * is simply reduced. If the quantities are the same then the item is
     * removed from the list.
     */
    public void voidLastItem() {
        for (SaleItem item : saleItems) {
            if (item.getProduct().getProductCode() == lastAdded.getProduct().getProductCode()) {
                if (item.getQuantity() > lastAdded.getQuantity()) { //If the quantities are different then reduce the quantity.
                    item.decreaseQuantity(lastAdded.getQuantity());
                    updateTotal();
                    return;
                } else if (item.getQuantity() == lastAdded.getQuantity()) { //If the quantities are the same then remove the item.
                    saleItems.remove(lastAdded);
                    updateTotal();
                    return;
                }
            }
        }
    }

    /**
     * This method will half the price of a sale item. If a sale item has a
     * quantity greater than 1, then all the times will be half priced. This
     * will also update the total price of the sale.
     *
     * @param item the item of half price.
     */
    public void halfPriceItem(SaleItem item) {
        for (SaleItem i : saleItems) {
            if (i.getProduct().getProductCode() == item.getProduct().getProductCode()) {
                if (i.getProduct().getPrice().compareTo(new BigDecimal("0.01")) != 0) {
                    if (i.getProduct().isOpen()) {
                        if (i.getProduct().getPrice().compareTo(item.getProduct().getPrice()) == 0) {
                            BigDecimal val = i.getPrice().divide(new BigDecimal("2"), BigDecimal.ROUND_DOWN);
                            i.setPrice(val);
                            updateTotal();
                            return;
                        }
                    } else {
                        BigDecimal val = i.getPrice().divide(new BigDecimal("2"), BigDecimal.ROUND_DOWN);
                        i.setPrice(val);
                        updateTotal();
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    public SaleItem getLastAdded() {
        return lastAdded;
    }

    private void updateTotal() {
        total = new BigDecimal("0");
        for (SaleItem item : saleItems) {
            total = total.add(item.getPrice());
        }
    }

    public String getSQLInsertStatement() {
        if (this.customer == null) { //If no customer was assigned then set the customer ID to -1
            return this.total
                    + ",-1"
                    + "," + discount.getId()
                    + ",'" + new Time(this.time).toString()
                    + "'," + this.chargeAccount;
        } else {
            return this.total
                    + "," + this.customer.getId()
                    + "," + discount.getId()
                    + ",'" + new Time(this.time).toString()
                    + "'," + this.chargeAccount;
        }
    }

    public String getSQLUpdateStatement() {
        if (this.customer == null) {
            return "UPDATE SALES"
                    + " SET PRICE=" + this.total
                    + ", SET CUSTOMER=" + this.customer.getId()
                    + ", TIMESTAMP='" + new Time(this.time).toString()
                    + "', CHARGE_ACCOUNT=" + this.chargeAccount
                    + " WHERE SALES.ID=" + this.code;
        } else {
            return "UPDATE SALES"
                    + " SET PRICE=" + this.total
                    + ", SET CUSTOMER=-1"
                    + ", TIMESTAMP='" + new Time(this.time).toString()
                    + "', CHARGE_ACCOUNT=" + this.chargeAccount
                    + " WHERE SALES.ID=" + this.code;
        }
    }

    @Override
    public String toString() {
        return this.code
                + "\n" + this.saleItems.size()
                + "\n" + this.total.toString();
    }
}
