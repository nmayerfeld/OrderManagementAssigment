package edu.yu.cs.intro.orderManagement;
import java.util.*;
/**
 * Takes orders, manages the warehouse as well as service providers
 */
public class OrderManagementSystem {
    protected Warehouse warehouse;
    protected Set<ServiceProvider>serviceProviders;
    private Set<Product> products;
    protected Set<Service> servicesProvidedByBusiness;
    private int defaultProductStockLevel;
    protected Map <Service, Set<ServiceProvider>> servicePairing;
    private Set<Service> doNotOffer;

    /**
     * Creates a new Warehouse instance and calls the other constructor
     *
     * @param products
     * @param defaultProductStockLevel
     * @param serviceProviders
     */
    public OrderManagementSystem(Set<Product> products, int defaultProductStockLevel,
                                 Set<ServiceProvider> serviceProviders) {
        this(products, defaultProductStockLevel,serviceProviders,new Warehouse());
    }

    /**
     * 1) populate the warehouse with the products.
     * 2) retrieve set of services provided by the ServiceProviders, to save it as the set of
     services the business can provide
     * 3) create map of services to the List of service providers that provide them
     *
     * @param products - set of products to populate the warehouse with
     * @param defaultProductStockLevel - the default number of products to stock for any product
     * @param serviceProviders - set of service providers and the services they provide, to
    make up the services arm of the business
     * @param warehouse - the warehouse that we will store our products in
     */
    public OrderManagementSystem(Set<Product> products, int defaultProductStockLevel,
                                 Set<ServiceProvider> serviceProviders, Warehouse warehouse) throws IllegalArgumentException {
        this.products= new HashSet<>();
        this.serviceProviders=new HashSet<>();
        this.servicesProvidedByBusiness=new HashSet<>();
        this.warehouse=warehouse;
        this.defaultProductStockLevel=defaultProductStockLevel;
        this.servicePairing=new HashMap<>();
        this.doNotOffer=new HashSet<>();
        for (Product p:products) {
            this.warehouse.addNewProductToWarehouse(p,defaultProductStockLevel);
        }
        for(ServiceProvider sp:serviceProviders) {
            servicesProvidedByBusiness.addAll(sp.getServices());
        }
        for(Service s:servicesProvidedByBusiness) {
            Set<ServiceProvider>providesThisService=new HashSet<>();
            for(ServiceProvider sp:serviceProviders) {
                if(sp.getServices().contains(s)) {
                    providesThisService.add(sp);
                }
            }
            servicePairing.put(s,providesThisService);
        }
    }

    /**
     * Accept an order:
     * 1) See if we have ServiceProviders for all Services in the order. If not, reject the order.
     * 2) See if we can fulfill all Items in the order. If so, place the product orders with the
     warehouse and handle the service orders inside this class
     * 2a) We CAN fulfill a product order if either the warehouse currently has enough quantity in
     stock OR if the product is NOT on the "do not restock" list.
     * In the case that the current quantity of a product is < the quantity in the order AND the
     product is NOT on the "do not restock" list, the order management system should
     * first instruct the warehouse to restock the item, and then tell the warehouse to fulfill this
     order.
     * 3) Mark the order as completed
     * 4) Update the busy status of service providers involved...
     * @throws IllegalArgumentException if any part of the order for PRODUCTS can’t be fulfilled
     * @throws IllegalStateException if any part of the order for SERVICES can’t be fulfilled
     */
    public void placeOrder(Order order) throws IllegalArgumentException,IllegalStateException {
        Set<Product> productsRequested=new HashSet<>();
        Set<Service> servicesRequested=new HashSet<>();
        //fill them
        for(Item i: order.order.keySet()) {
            if(i instanceof Product) {
                productsRequested.add((Product)i);
            }
            else {
                servicesRequested.add((Service)i);
            }
        }
        //check and process services
        if(validateServices(servicesRequested,order)!=0) {
            throw new IllegalStateException("can't process services requested: "+validateServices(servicesRequested,order));
        }
        processServicesInOrder(order,servicesRequested);
        //check and process products
        if(validateProducts(productsRequested,order)!=0) {
            throw new IllegalArgumentException("cant process product requested: "+validateProducts(productsRequested,order));
        }
        processProductsInOrder(order,productsRequested);
        //update the busy ppl
        for(ServiceProvider sp:this.serviceProviders) {
            if(sp.busy==true) {
                sp.setOrdersSinceBusy(sp.getOrdersSinceBusy()+1);{
                    if(sp.getOrdersSinceBusy()==4) {
                        sp.endCustomerEngagement();
                        sp.setOrdersSinceBusy(0);
                    }
                }
            }
        }
        order.setCompleted(true);
    }

    /**
     * @param order
     * @param services
     * @throws IllegalStateException
     */
    protected void processServicesInOrder(Order order, Set<Service>services) throws IllegalStateException {
        for(Service i:services) {
            int amount=order.order.get(i);
            int counter=0;
            for(ServiceProvider sp:this.servicePairing.get(i)) {
                if(counter<amount) {
                    if(sp.busy==false) {
                        counter++;
                        sp.assignToCustomer();
                    }
                }
            }
            if(counter<amount) {
                throw new IllegalStateException("not enough providers for the amount requested of this service");
            }
        }
    }

    /**
     * @param order
     * @param products
     * @throws IllegalArgumentException
     */
    protected void processProductsInOrder(Order order, Set<Product>products) throws IllegalArgumentException {
        for(Product i:products) {
            // if there's enough in stock, fulfill order
            if(this.warehouse.canFulfill(i.getItemNumber(),order.order.get(i))) {
                this.warehouse.fulfill(i.getItemNumber(),order.order.get(i));
            }
            // if there isn't enough in stock, and not on do not restock list, fulfill order
            else if (this.warehouse.isRestockable(i.getItemNumber())) {
                this.warehouse.restock(i.getItemNumber(),order.order.get(i));
                this.warehouse.fulfill(i.getItemNumber(),order.order.get(i));
            }
        }
    }

    /**
     * Validate that all the services being ordered can be provided. Make sure to check how many instances of a given service are being requested in
     the order, and see if we have enough providers for them.
     * @param services the set of services which are being ordered inside the order
     * @param order the order whose services we are validating
     * @return itemNumber of the first requested service encountered that we either do not have a provider for at all, or for which we do not have an
    available provider. Return 0 if all services are valid.
     */
    protected int validateServices(Collection<Service> services, Order order) {
        Set<ServiceProvider>sPAvailable=new HashSet<>();
        //add service providers
        for(Service s:servicesProvidedByBusiness) {
            for(ServiceProvider sp: this.servicePairing.get(s)) {
                sPAvailable.add(sp);
            }
        }
        for(Service s: services) {
            if(this.servicesProvidedByBusiness.contains(s)) {
                int amount=order.order.get(s);
                int counter=0;
                for(ServiceProvider sp:this.servicePairing.get(s)) {
                    if(sp.busy==false&&sPAvailable.contains(sp)) {
                        counter++;
                        sPAvailable.remove(sp);
                    }
                }
                if (counter<amount) {
                    return s.getItemNumber();
                }
            }
            else {
                return s.getItemNumber();
            }
        }
        return 0;
    }

    /**
     * validate that the requested quantity of products can be fulfilled
     * @param products being ordered in this order
     * @param order the order whose products we are validating
     * @return itemNumber of product which is either not in the catalog or which we have insufficient quantity of. Return 0 if we can fulfill.
     */
    protected int validateProducts(Collection<Product> products, Order order) {
        //im returning after the first product works, not iterating throu all of them
        for(Product i:products) {
            if(!(this.warehouse.canFulfill(i.getItemNumber(),order.order.get(i))||this.warehouse.isRestockable(i.getItemNumber()))) {
                return i.getItemNumber();
            }
        }
        return 0;
    }

    /**
     * Adds new Products to the set of products that the warehouse can ship/fulfill
     * @param products the products to add to the warehouse
     * @return set of products that were actually added (don't include any products that were already in the warehouse before this was called!)
     */
    protected Set<Product> addNewProducts(Collection<Product> products) {
        Set<Product>productsAdded=new HashSet<>();
        for(Product p: products) {
            try{
                int amount=defaultProductStockLevel;
                if(this.warehouse.defaultStockLevels.get(p)!=null) {
                    amount=this.warehouse.defaultStockLevels.get(p);
                }
                this.warehouse.addNewProductToWarehouse(p,amount);
                productsAdded.add(p);
            }catch(IllegalArgumentException e){
                continue;
            }
        }
        return productsAdded;
    }

    /**
     * Adds an additional ServiceProvider to the system. Update all relevant data about which Services are offered and which ServiceProviders provide
     which services
     * @param provider the provider to add
     */
    protected void addServiceProvider(ServiceProvider provider) {
        serviceProviders.add(provider);
        //check if on do not provide list
        for(Service s:provider.getServices()) {
            if(!doNotOffer.contains(s)) {
                servicesProvidedByBusiness.add(s);
            }
        }
        //update list of ppl who provide this service by adding to the list in the hashmap pairing
        for(Service s:servicesProvidedByBusiness) {
            Set<ServiceProvider>providesThisService=new HashSet<>();
            if(provider.getServices().contains(s)) {
                providesThisService.add(provider);
            }
            if (servicePairing.get(s)==null) {
                servicePairing.put(s, providesThisService);
            } else {
                Set<ServiceProvider> set = servicePairing.get(s);
                set.addAll(providesThisService);
                servicePairing.put(s, set);
            }
        }
    }

    /**
     *
     * @return get the set of all the products offered/sold by this business
     */
    public Set<Product> getProductCatalog() {
        return this.warehouse.actualStockLevels.keySet();
    }

    /**
     * @return get the set of all the Services offered/sold by this business
     */
    public Set<Service> getOfferedServices() {
        return this.servicesProvidedByBusiness;
    }

    /**
     * Discontinue Item, i.e. stop selling a Service or Product.
     * Also prevent the Item from being added in the future.
     * If it's a Service - remove it from the set of provided services.
     * If it's a Product - still sell whatever instances of this Product are in stock, but do not restock it.
     * @param item the item to discontinue see {@link Item}
     */
    protected void discontinueItem(Item item) {
        if(item instanceof Product) {
            this.warehouse.doNotRestock.add((Product)item);
        }
        else {
            this.servicesProvidedByBusiness.remove((Service)item);
            this.doNotOffer.add((Service)item);
        }
    }

    /**
     * Set the default product stock level for the given product
     * @param prod
     * @param level
     */
    protected void setDefaultProductStockLevel(Product prod, int level)
    {
        this.warehouse.defaultStockLevels.put(prod,level);
    }
}