package org.fm.controller;

import org.fm.dao.OrderPersistenceException;
import org.fm.dto.Order;
import org.fm.dto.Product;
import org.fm.dto.TaxInfo;
import org.fm.service.FlooringServiceLayer;
import org.fm.service.NoOrdersOnDateException;
import org.fm.service.OrderDataValidationException;
import org.fm.ui.FlooringView;

import java.time.LocalDate;
import java.util.List;

public class FlooringController {

    private FlooringServiceLayer service;
    private FlooringView view;

    public FlooringController(FlooringServiceLayer service, FlooringView view) {
        this.service = service;
        this.view = view;
    }

    public void run() {
        boolean keepGoing = true;
        int menuSelection;

        while (keepGoing) {
            menuSelection = view.printMenuAndGetSelection();
            switch (menuSelection) {
                case 1: displayOrders(); break;
                case 2: addOrder();      break;
                case 3: editOrder();     break;
                case 4: removeOrder();   break;
                case 5: exportAllData(); break;
                case 6: keepGoing = false; break;
                default: view.displayUnknownCommand();
            }
        }
        view.displayExitBanner();
    }
    private void displayOrders() {
        LocalDate date = view.getDateInput("Enter the date to display orders for");
        try {
            List<Order> orders = service.getOrdersByDate(date);
            view.displayOrders(orders);
        } catch (NoOrdersOnDateException e) {
            view.displayErrorMessage("No orders for that date");
        } catch (OrderPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void addOrder() {
        try {
            List<Product> products = service.getAllProducts();
            List<TaxInfo> taxes = service.getAllTaxes();

            LocalDate date = view.getFutureDate();
            Order newOrder = view.getNewOrderInfo(products, taxes);

            // Calculate orders before showing summary to user
            newOrder = service.calculateOrder(newOrder);
            view.displayOrder(newOrder);

            if (view.getPlaceOrderConfirmation()) {
                service.addOrder(date, newOrder);
                view.displaySuccessBanner("Order successfully added.");
            } else {
                view.displayErrorMessage("Order was not saved.");
            }
        } catch (OrderPersistenceException | OrderDataValidationException | NoOrdersOnDateException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void editOrder() {
        LocalDate date = view.getDateInput("Enter the order date");
        int orderNumber = view.getOrderNumber();

        try {
            // find the existing order
            List<Order> orders = service.getOrdersByDate(date);
            Order existing = orders.stream()
                    .filter(o -> o.getOrderNumber() == orderNumber)
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                view.displayErrorMessage("Order #" + orderNumber + " not found for that date.");
                return;
            }

            List<Product> products = service.getAllProducts();
            List<TaxInfo> taxes = service.getAllTaxes();

            // let the user edit fields, keeping existing values on blank entry
            Order edited = view.getEditOrderInfo(existing, products, taxes);

            // recalculate with any changed values
            edited = service.calculateOrder(edited);
            view.displayOrder(edited);

            if (view.getSaveEditConfirmation()) {
                service.editOrder(date, edited);
                view.displaySuccessBanner("Order successfully updated.");
            } else {
                view.displayMessage("Changes were not saved.");
            }
        } catch (NoOrdersOnDateException | OrderDataValidationException | OrderPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }
    private void removeOrder() {
         LocalDate date = view.getDateInput("Enter the order date");
        int orderNumber = view.getOrderNumber();

        try {
            // find and display the order before asking for confirmation
            List<Order> orders = service.getOrdersByDate(date);
            Order toRemove = orders.stream()
                    .filter(o -> o.getOrderNumber() == orderNumber)
                    .findFirst()
                    .orElse(null);

            if (toRemove == null) {
                view.displayErrorMessage("Order #" + orderNumber + " not found for that date.");
                return;
            }

            view.displayOrder(toRemove);

            if (view.getRemoveConfirmation()) {
                service.removeOrder(date, orderNumber);
                view.displaySuccessBanner("Order successfully removed.");
            } else {
                view.displayMessage("Order was not removed.");
            }
        } catch (NoOrdersOnDateException | OrderDataValidationException | OrderPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void exportAllData() {
        try {
            service.exportAllData();
            view.displaySuccessBanner("All data exported to Backup/DataExport.txt.");
        } catch (OrderPersistenceException e) {
            view.displayErrorMessage("Export failed: " + e.getMessage());
        }
    }
}
