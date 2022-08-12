package edu.yu.cs.intro.orderManagement;
import java.util.*;
public class Product implements Item {
    private String name;
    private double price;
    protected int productID;

    /**
     * @param name
     * @param price
     * @param productID
     */
    public Product(String name, double price, int productID) {
        this.name=name;
        this.price=price;
        this.productID=productID;
    }

    @Override
    public int getItemNumber() {
        return this.productID;
    }

    @Override
    public String getDescription() {
        return this.name;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public boolean equals(Object o) {
        //see if it's the same object
        if(this == o) {
            return true;
        }
        //see if it's null
        if(o == null) {
            return false;
        }
        //see if they're from the same class
        if(this.getClass()!=o.getClass()) {
            return false;
        }
        // cast and check for equality
        Product otherProduct = (Product) o;
        if (this.getItemNumber()!=otherProduct.getItemNumber()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        Integer pID= (Integer)this.productID;
        return pID.hashCode();
    }
}