package org.fm.ui;

import org.fm.dto.Order;
import org.fm.dto.Product;
import org.fm.dto.TaxInfo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class FlooringView {
    private UserIO io;

    public FlooringView(UserIO io) {this.io = io;}

    public int printMenuAndGetSelection() {
        io.print("\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print("* <<Flooring Program>>");
        io.print("* 1. Display Orders");
        io.print("* 2. Add an Order");
        io.print("* 3. Edit an Order");
        io.print("* 4. Remove an Order");
        io.print("* 5. Export All Data");
        io.print("* 6. Quit");
        io.print("*");
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");

        return io.readInt("Please select from the above choices.", 1, 6);
    }

    public void displayOrders(List<Order> orders) {
        io.print("\n--- Orders ---");
        orders.forEach(o -> displayOrder(o));
    }

    public void displayOrder(Order o) {
        io.print("Order #: " + o.getOrderNumber());
        io.print("  Customer:  " + o.getCustomerName());
        io.print("  State:     " + o.getTaxInfo().getStateAbbreviation());
        io.print("  Product:   " + o.getProduct().getProductType());
        io.print("  Area:      " + o.getArea() + " sq ft");
        io.print("  Material:  $" + o.getMaterialCost());
        io.print("  Labor:     $" + o.getLaborCost());
        io.print("  Tax:       $" + o.getTax());
        io.print("  Total:     $" + o.getTotal());
    }

    public Order getNewOrderInfo(List<Product> products, List<TaxInfo> taxes) {
        Order order = new Order(0);

        // Customer name
        while (true) {
            String name = io.readString("Enter customer name: ");
            if(name != null && !name.isBlank() && name.matches("[a-zA-Z0-9., ]+")) {
                order.setCustomerName(name);
                break;
            }
            io.print("Invalid name. Only letters, numbers, periods, and commas allowed.");
        }

        // State
        displayTaxes(taxes);
        while (true) {
            String state = io.readString("Enter state abbreviation: ").toUpperCase();
            boolean validState = taxes.stream().anyMatch(t -> t.getStateAbbreviation().equals(state));
            if(validState) {
                order.getTaxInfo().setStateAbbreviation(state);
                break;
            }
            io.print("We do not sell in that state");
        }

        // Product type
        displayProducts(products);
        while (true) {
            String type = io.readString("Enter product type: ");
            boolean validProductType = products.stream().anyMatch(p -> p.getProductType().equalsIgnoreCase(type));
            if (validProductType) {
                order.getProduct().setProductType(type);
                break; }
            io.print("Invalid product type. Please choose from the list above.");
        }

        // Area
        while (true) {
            String areaStr = io.readString("Enter area (minimum 100 sq ft): ");
            try {
                BigDecimal area = new BigDecimal(areaStr);
                //Check if area is at least 100 sq ft
                if (area.compareTo(new BigDecimal("100")) >= 0) {
                    order.setArea(area);
                    break; }
                io.print("Area must be at least 100 sq ft.");
            } catch (NumberFormatException e) {
                io.print("Please enter a valid number.");
            }
        }

        return order;
    }

    public Order getEditOrderInfo(Order currentOrder, List<Product> products, List<TaxInfo> taxes) {
        // Customer name
        String name = io.readString("Enter customer name (" + currentOrder.getCustomerName() + "): ");
        if (!name.isBlank()) {
            // If name is valid input
            if (name.matches("[a-zA-Z0-9., ]+"))
                currentOrder.setCustomerName(name);
            else
                io.print("Invalid name, keeping the original value.");
        }

        // State
        displayTaxes(taxes);
        String state = io.readString("Enter state (" + currentOrder.getTaxInfo().getStateAbbreviation() + "): ");
        if (!state.isBlank()) {
            boolean validState = taxes.stream().anyMatch(tax -> tax.getStateAbbreviation().equalsIgnoreCase(state));
            if (validState)
                currentOrder.getTaxInfo().setStateAbbreviation(state);
            else
                io.print("Invalid state, keeping the original value.");
        }

        // Product type
        displayProducts(products);
        String productType = io.readString("Enter product type (" + currentOrder.getProduct().getProductType() + "): ");
        if (!productType.isBlank()) {
            boolean validProductType = products.stream().anyMatch(product -> product.getProductType().equalsIgnoreCase(productType));
            if (validProductType)
                currentOrder.getProduct().setProductType(productType);
            else
                io.print("Invalid product type, keeping the original value");
        }

        // Area
        String areaStr = io.readString("Enter area (" + currentOrder.getArea() + "): ");
        if(!areaStr.isBlank()) {
            try {
                BigDecimal area = new BigDecimal(areaStr);
                if (area.compareTo(new BigDecimal("100")) >= 0)
                    currentOrder.setArea(area);
                else
                    io.print("Area too small, keeping the original value.");
            } catch (NumberFormatException e) {
                io.print("Invalid number, keeping the original");
            }
        }
        return currentOrder;
    }

    private void displayTaxes(List<TaxInfo> taxes) {
        io.print("\nAvailable States:");
        taxes.forEach(tax ->
                io.print(String.format("  %s - %s (%.2f%%)", tax.getStateAbbreviation(), tax.getStateName(), tax.getTaxRate())));
    }

    private void displayProducts(List<Product> products) {
        io.print("\nAvailableProducts");
        products.forEach(product -> io.print(String.format("%-15s Material: $%-8s Labor: $%s",
                product.getProductType(), product.getCostPerSquareFoot(), product.getLaborCostPerSquareFoot())));
    }

    public boolean getPlaceOrderConfirmation() {
        String input = io.readString("Place this order? (Y/N): ");
        return input.trim().equalsIgnoreCase("Y");
    }

    public static void main(String[] args) {
        Order o = new Order(1);
        o.setCustomerName("Doctor Who");

        TaxInfo taxInfo = new TaxInfo();
        taxInfo.setStateAbbreviation("WA");
        taxInfo.setStateName("Washington");
        taxInfo.setTaxRate(new BigDecimal("9.25"));

        o.setTaxInfo(taxInfo);

        Product product = new Product();
        product.setProductType("Wood");
        product.setCostPerSquareFoot(new BigDecimal("5.15"));
        product.setLaborCostPerSquareFoot(new BigDecimal("4.75"));

        o.setProduct(product);

        o.setMaterialCost(new BigDecimal("1251.45"));
        o.setLaborCost(new BigDecimal("1154.25"));
        o.setTax(new BigDecimal("216.51"));
        o.setTotal(new BigDecimal("2622.21"));

        Order o2 = new Order(2);
        o2.setCustomerName("Doctor Who2");

        TaxInfo taxInfo2 = new TaxInfo();
        taxInfo2.setStateAbbreviation("WA2");
        taxInfo2.setStateName("Washington2");
        taxInfo2.setTaxRate(new BigDecimal("9.252"));

        o2.setTaxInfo(taxInfo);

        Product product2 = new Product();
        product2.setProductType("Wood2");
        product2.setCostPerSquareFoot(new BigDecimal("5.152"));
        product2.setLaborCostPerSquareFoot(new BigDecimal("4.752"));

        o2.setProduct(product2);

        o2.setMaterialCost(new BigDecimal("1251.452"));
        o2.setLaborCost(new BigDecimal("1154.252"));
        o2.setTax(new BigDecimal("216.512"));
        o2.setTotal(new BigDecimal("2622.212"));

        FlooringView fv = new FlooringView(new UserIOConsoleImpl());
        //Order o3 = fv.getNewOrderInfo(Arrays.asList(product, product2), Arrays.asList(taxInfo, taxInfo2));
        //fv.displayOrders(Arrays.asList(o,o2,o3));

        fv.displayOrder(o);
        fv.displayOrder(fv.getEditOrderInfo(o,Arrays.asList(product, product2), Arrays.asList(taxInfo, taxInfo2)));
    }
}
