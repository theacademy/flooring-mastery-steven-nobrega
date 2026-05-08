package org.fm.ui;

import org.fm.dto.Order;
import org.fm.dto.Product;
import org.fm.dto.TaxInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class FlooringView {
    private UserIO io;
    private static final DateTimeFormatter INPUT_FORMAT  = DateTimeFormatter.ofPattern("MM-dd-yyyy");

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
                    break;
                }
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

    public LocalDate getDateInput(String prompt) {
        while (true) {
            String input = io.readString(prompt + " (MM-DD-YYYY): ");
            try {
                return LocalDate.parse(input.trim(), INPUT_FORMAT);
            } catch (DateTimeParseException e) {
                io.print("Invalid date format. Please use MM-DD-YYYY.");
            }
        }
    }

    public LocalDate getFutureDate() {
        while (true) {
            LocalDate date = getDateInput("Enter order date");
            if (date.isAfter(LocalDate.now()))
                return date;
            io.print("Order date must be in the future.");
        }
    }

    public boolean getPlaceOrderConfirmation() {
        String input = io.readString("Place this order? (Y/N): ");
        return input.trim().equalsIgnoreCase("Y");
    }

    public boolean getSaveEditConfirmation() {
        String input = io.readString("Save changes? (Y/N): ");
        return input.trim().equalsIgnoreCase("Y");
    }

    public boolean getRemoveConfirmation() {
        String input = io.readString("Are you sure you want to remove this order? (Y/N): ");
        return input.trim().equalsIgnoreCase("Y");
    }

    public int getOrderNumber() {
        return io.readInt("Enter order number: ");
    }

    public void displayUnknownCommand() {
        io.print("Unknown command. Please try again.");
    }

    public void displayExitBanner() {
        io.print("Good bye!");
    }

    public void displayErrorMessage(String message) {
        io.print("\n=== ERROR ===");
        io.print(message);
    }

    public void displaySuccessBanner(String message) {
        io.print("\n=== SUCCESS ===");
        io.print(message);
    }

    public void displayMessage(String message) {
        io.print(message);
    }
}
