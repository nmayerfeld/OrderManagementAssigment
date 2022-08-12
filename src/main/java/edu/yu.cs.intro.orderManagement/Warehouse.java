package edu.yu.cs.intro.orderManagement;
import java.util.*;
/**
 * Stocks products, fulfills product orders, manages stock of products.
 */
public class Warehouse {
    /**
     * create a warehouse, initialize all the instance variables
     */
    protected Map<Product, Integer> defaultStockLevels;
    protected Map<Product, Integer> actualStockLevels;
    protected Set<Product> doNotRestock;

    protected Warehouse() {
        defaultStockLevels= new HashMap<>();
        actualStockLevels= new HashMap<>();
        doNotRestock= new HashSet<>();
    }

    /**
     * @return all unique Products stocked in the warehouse
     */
    protected Set<Product> getAllProductsInCatalog() {
        Set<Product> productsInStock= new HashSet<>();
        for(Product p: actualStockLevels.keySet()) {
            if(actualStockLevels.get(p)>0) {
                productsInStock.add(p);
            }
        }
        return productsInStock;
    }

    /**
     * Add a product to the warehouse, at the given stock level.
     * @param product
     * @param desiredStockLevel the number to stock initially, and also to restock to when
    subsequently restocked
     * @throws IllegalArgumentException if the product is in the "do not restock" set, or if the
    product is already in the warehouse
     */
    protected void addNewProductToWarehouse(Product product, int desiredStockLevel) throws IllegalArgumentException {
        if(doNotRestock.contains(product)||(actualStockLevels.keySet().contains(product))) {
            throw new IllegalArgumentException("cannot add product to warehouse");
        }
        actualStockLevels.put(product,desiredStockLevel);
        defaultStockLevels.put(product,desiredStockLevel);
    }

    /**
     * If the actual stock is already >= the minimum, do nothing. Otherwise, raise it to minimum OR
     the default stock level, whichever is greater
     * @param productNumber
     * @param minimum
     * @throws IllegalArgumentException if the product is in the "do not restock" set, or if it is
    not in the catalog
     */
    protected void restock(int productNumber, int minimum) throws IllegalArgumentException {
        Product p=null;
        for(Product product: actualStockLevels.keySet()) {
            if(product.getItemNumber()==productNumber) {
                p=product;
            }
        }
        if(p==null||doNotRestock.contains(p)) {
            throw new IllegalArgumentException("cannot restock product");
        }
        int currentStock=actualStockLevels.get(p);
        if(minimum>currentStock&&minimum>defaultStockLevels.get(p)) {
            actualStockLevels.put(p,minimum);
        }
        else if(defaultStockLevels.get(p)>currentStock&&defaultStockLevels.get(p)>minimum) {
            actualStockLevels.put(p,defaultStockLevels.get(p));
        }
    }

    /**
     /*
     * Set the new default stock level for the given product
     * @param productNumber
     * @param quantity
     * @return the old default stock level
     * @throws IllegalArgumentException if the product is in the "do not restock" set, or if it is
    not in the catalog
     */
    protected int setDefaultStockLevel(int productNumber, int quantity) throws IllegalArgumentException {
        Product p=null;
        for(Product product: actualStockLevels.keySet()) {
            if(product.getItemNumber()==productNumber) {
                p=product;
            }
        }
        if(p==null||doNotRestock.contains(p)) {
            throw new IllegalArgumentException("cannot restock product");
        }
        int oldStockDefault=defaultStockLevels.get(p);
        defaultStockLevels.put(p,quantity);
        return oldStockDefault;
    }
    /**
     * @param productNumber
     * @return how many of the given product we have in stock, or zero if it is not stocked
     */
    protected int getStockLevel(int productNumber) {
        Product p=null;
        for(Product product: actualStockLevels.keySet()) {
            if(product.getItemNumber()==productNumber) {
                p=product;
            }
        }
        if(p==null) {
            return 0;
        }
        return actualStockLevels.get(p);
    }

    /**
     * @param itemNumber
     * @return true if the given item number is in the warehouse's catalog, false if not
     */
    protected boolean isInCatalog(int itemNumber) {
        Product p=null;
        for(Product product: actualStockLevels.keySet()) {
            if(product.getItemNumber()==itemNumber) {
                p=product;
            }
        }
        if(p==null) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param itemNumber
     * @return false if it's not in catalog or is in the "do not restock" set. Otherwise true.
     */
    protected boolean isRestockable(int itemNumber) {
        Product p=null;
        for(Product product: actualStockLevels.keySet()) {
            if(product.getItemNumber()==itemNumber) {
                p=product;
            }
        }
        if(p==null||doNotRestock.contains(p)) {
            return false;
        }
        return true;
    }

    /**
     * add the given product to the "do not restock" set
     * @param productNumber
     * @return the current actual stock level of the product
     */
    protected int doNotRestock(int productNumber) {
        Product p=null;
        for(Product product: actualStockLevels.keySet()) {
            if(product.getItemNumber()==productNumber) {
                p=product;
            }
        }
        doNotRestock.add(p);
        return actualStockLevels.get(p);
    }

    /**
     * can the warehouse fulfill an order for the given amount of the given product?
     * @param productNumber
     * @param quantity
     * @return false if the product is not in the catalog or there are fewer than quantity of the
    products in the catalog. Otherwise true.
     */
    protected boolean canFulfill(int productNumber, int quantity) {
        Product p=null;
        for(Product product: actualStockLevels.keySet()) {
            if(product.getItemNumber()==productNumber) {
                p=product;
            }
        }
        if(p==null||actualStockLevels.get(p)<quantity) {
            return false;
        }
        return true;
    }

    /**
     * Fulfill an order for the given amount of the given product, i.e. lower the stock levels of
     the product by the given amount
     * @param productNumber
     * @param quantity
     * @throws IllegalArgumentException if {@link #canFulfill(int, int)} returns false
     */
    protected void fulfill(int productNumber, int quantity) throws IllegalArgumentException {
        if(!canFulfill(productNumber,quantity)) {
            throw new IllegalArgumentException("cannot fulfill requested order");
        }
        Product p=null;
        for(Product product: actualStockLevels.keySet()) {
            if(product.getItemNumber()==productNumber) {
                p=product;
            }
        }
        actualStockLevels.put(p,actualStockLevels.get(p)-quantity);
    }
}