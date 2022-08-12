package edu.yu.cs.intro.orderManagement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class Demo {
    private Set<Product> products;
    private Set<ServiceProvider> providers;
    private Set<Service> allServices;
    private Map<Integer, Product> idToProduct;
    private Map<Integer, Service> idToService;
    private Warehouse warehouse;
    public static void main(String[] args) {
        Demo dd = new Demo();
        dd.runDemo();
    }
    public Demo(){
        this.warehouse = new Warehouse();
        this.products = new HashSet<>();
        this.idToProduct = new HashMap<>();
        this.idToService = new HashMap<>();
        this.allServices = new HashSet<>();
        this.providers = new HashSet<>();
    }
    void runDemo(){
        OrderManagementSystem oms = new OrderManagementSystem(this.products,5,this.providers,this.warehouse);
        //populate our system with products and services
        Product p1= new Product("apples",1,5);
        Product p2=new Product("oranges",2,2);
        Product prod1= new Product("prod1",1,1);
        Product p3= new Product("pears",3,3);
        Set<Product> myProduct=new HashSet<>();
        myProduct.add(p1);
        myProduct.add(p2);
        myProduct.add(prod1);
        oms.addNewProducts(myProduct);

//test what's there - should be apples and oranges
        Set<Product> productsPresent=new HashSet<>();
        productsPresent.addAll(oms.getProductCatalog());
        for(Product p: productsPresent)
        {
            System.out.println(p);
        }

//create new order with product p3 that doesn't exist
        Order o1= new Order();
        o1.addToOrder(p3,3);
        System.out.println(o1.getItems());

//place order o1- should throw illegalargumentexception
        try
        {
            oms.placeOrder(o1);
            assert false;
        }catch(IllegalArgumentException e){
            System.out.println("first test that i failed just passed but i din't change anything");
        }

//test 3 that failed- add product already in warehouse
        Warehouse w1=new Warehouse();
        w1.addNewProductToWarehouse(p1,5);
        try{
            w1.addNewProductToWarehouse(p1,5);
        }catch (IllegalArgumentException e){
            System.out.println("solved the issue with the 3rd test");
        }



//create a few services
        Service s1= new Service(1.0,1,1,"cooking");
        Service s2= new Service(2.0,2,2,"cleaning");
        Service s3= new Service(3.0,3,3,"laundry");
        Set<Service> services1= new HashSet<>();
        services1.add(s1);
        services1.add(s2);
        services1.add(s3);

//create a few service providers
        ServiceProvider sp1=new ServiceProvider("Noam",1,services1);
        ServiceProvider sp2=new ServiceProvider("Meir",2,services1);
        ServiceProvider sp3=new ServiceProvider("joseph",3,services1);

//add them to the order management system
        oms.addServiceProvider(sp1);
        oms.addServiceProvider(sp2);
        oms.addServiceProvider(sp3);

//check that it worked
        for(Service s: oms.getOfferedServices())
        {
            System.out.println(s);
            for (ServiceProvider sp:oms.servicePairing.get(s))
            {
                System.out.println(sp);
            }
        }

//create an order for more of s1 than providers that exist
        Order o2=new Order();
        Set<Service>spTest=new HashSet<>();
        spTest.add(s1);
        o2.addToOrder(s1,4);
//try to place the order
        try{
            oms.placeOrder(o2);
            assert false;
        }catch(IllegalStateException e)
        {
            System.out.println("failed to place order. YAY!!!");
        }
        // try validate services
        System.out.println(oms.validateServices(spTest,o2));
        System.out.println("y did he fail my test 2- it just worked");

//omer's test
        Order o3=new Order();
        o3.addToOrder(s1,3);
        oms.placeOrder(o3);

        Order o4=new Order();
        o4.addToOrder(s2,1);
        Set<Service> omerTest=new HashSet<>();
        omerTest.add(s2);
        System.out.println(oms.validateServices(omerTest,o4));


//judah's nonexistant product test
        OrderManagementSystem system = new OrderManagementSystem(this.products,5,this.providers);
        Order order = new Order();
        order.addToOrder(new Product("prod1",1,1),1);
        order.addToOrder(new Product("prod88",88,88),1); //no such product
        try {
            system.placeOrder(order);
            throw new IllegalStateException("Should NOT have been able to fulfill the order due to there being no such product, and thus should've thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e)
        {
            System.out.println("it worked");
        }
    }
}