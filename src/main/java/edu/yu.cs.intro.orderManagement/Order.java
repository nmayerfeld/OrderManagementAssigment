package edu.yu.cs.intro.orderManagement;
import java.util.*;
/**
 * Represents an order placed by a customer. An item in the order can be an instance of either
 Product or Service
 */
public class Order {
    protected Map <Item, Integer> order;
    private boolean isCompleted;
    public Order() {
        order=new HashMap<>();
        isCompleted=false;
    }

    /**
     * @return all the items (products and services) in the order
     */
    public Item[] getItems() {
        return order.keySet().toArray(new Item[order.keySet().size()]);
    }

    /**
     * @param b
     * @return the quantity of the given item ordered in this order. Zero if the item is not in the
    order.
     */
    public int getQuantity(Item b) {
        return this.order.get(b);
    }

    /**
     * Add the given quantity of the given item (product or service) to the order
     * @param item
     * @param quantity
     */
    public void addToOrder(Item item, int quantity) {
        this.order.put(item,quantity);
    }

    /**
     * Calculate the total price of PRODUCTS in the order. Must multiply each item's price by the
     quantity.
     * @return the total price of products in this order
     */
    public double getProductsTotalPrice() {
        Double totalPrice=0.0;
        for (Item item : this.order.keySet()) {
            if(item instanceof Product) {
                totalPrice+=item.getPrice()*order.get(item);
            }
        }
        return totalPrice;
    }

    /**
     * Calculate the total price of the SERVICES in the order. Must multiply each item's price by
     the quantity.
     * @return the total price of products in this order
     */
    public double getServicesTotalPrice() {
        Double totalPrice=0.0;
        for (Item item : this.order.keySet()) {
            if(item instanceof Service) {
                totalPrice+=item.getPrice()*order.get(item);
            }
        }
        return totalPrice;
    }

    /**
     * @return has the order been completed by the order management system?
     */
    public boolean isCompleted() {
        return this.isCompleted;
    }

    /**
     * Indicate if the order has been completed by the order management system
     * @param completed
     */
    public void setCompleted(boolean completed) {
        this.isCompleted=completed;
    }
}
