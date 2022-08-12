package edu.yu.cs.intro.orderManagement;
import java.util.*;
/**
 * An implementation of item which represents a Service provided by the business.
 * Has a price per billable hour as well a number of hours this service takes.
 * The price returned by getPrice must be the per hour price multiplied by the number of hours the
 service takes
 */
public class Service implements Item {
    private double pricePerHour;
    private int numberOfHours;
    protected int serviceID;
    private String description;

    /**
     * @param pricePerHour
     * @param numberOfHours
     * @param serviceID
     * @param description
     */
    public Service(double pricePerHour, int numberOfHours, int serviceID, String description) {
        this.pricePerHour=pricePerHour;
        this.numberOfHours=numberOfHours;
        this.serviceID=serviceID;
        this.description=description;
    }

    /**
     * @return the number of hours this service takes
     */
    public int getNumberOfHours() {
        return this.numberOfHours;
    }

    @Override
    public int getItemNumber() {
        return this.serviceID;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public double getPrice() {
        return this.pricePerHour*this.numberOfHours;
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
        Service otherProduct = (Service) o;
        if (this.getItemNumber()!=otherProduct.getItemNumber()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        Integer sID= (Integer)this.serviceID;
        return sID.hashCode();
    }
}
