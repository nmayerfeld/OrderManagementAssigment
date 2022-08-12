package edu.yu.cs.intro.orderManagement;
import java.util.*;
/**
 * 1) has a Set of services that it can provide
 * 2) can only work on one order at a time - once assigned to a customer, can’t take another
 assignment until 3 other orders have been placed with the order management system
 * 3) is uniquely identified by its ID
 */
public class ServiceProvider implements Comparable<ServiceProvider>{
    private String name;
    protected int id;
    protected boolean busy;
    protected int ordersSinceBusy;
    protected Set<Service> services;
    /**
     *
     * @param name
     * @param id unique id of the ServiceProvider
     * @param services set of services this provider can provide
     */
    public ServiceProvider(String name, int id, Set<Service> services) {
        this.name=name;
        this.id=id;
        this.services=services;
        this.busy=false;
        this.ordersSinceBusy=0;
    }

    /**
     * @return orders elapsed since became busy
     */
    protected int getOrdersSinceBusy() {
        return this.ordersSinceBusy;
    }

    /**
     * @param i
     */
    protected void setOrdersSinceBusy(int i) {
        this.ordersSinceBusy=i;
    }

    /**
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Assign this provider to a customer. Record the fact that he is busy.
     * @throws IllegalStateException if the provider is currently assigned to a job
     */
    protected void assignToCustomer() throws IllegalStateException {
        if(busy==true) {
            throw new IllegalStateException("Already assigned to a customer");
        }
        busy=true;
    }

    /**
     * Free this provider up - is no longer assigned to a customer
     * @throws IllegalStateException if the provider is NOT currently assigned to a job
     */
    protected void endCustomerEngagement() throws IllegalStateException {
        if(busy==false) {
            throw new IllegalStateException ("Not currently assigned to a customer");
        }
        busy=false;
    }

    /**
     * @param s add the given service to the set of services this provider can provide
     * @return true if it was added, false if not
     */
    protected boolean addService(Service s) {
        return this.services.add(s);
    }

    /**
     * @param s remove the given service from the set of services this provider can provide
     * @return true if it was removed, false if not
     */
    protected boolean removeService(Service s) {
        return this.services.remove(s);
    }

    /**
     *
     * @return a COPY of the set of services. MUST NOT return the Set instance itself, since that
    would allow a caller to then add/remove services to/from the set
     */
    public Set<Service> getServices() {
        Set<Service> copy=new HashSet<>(this.services);
        return copy;
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
        ServiceProvider otherProduct = (ServiceProvider) o;
        if (this.getId()!=otherProduct.getId()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        Integer spID= (Integer)this.id;
        return spID.hashCode();
    }
    @Override
    public int compareTo(ServiceProvider other) {
        return this.id-other.id;
    }
}
