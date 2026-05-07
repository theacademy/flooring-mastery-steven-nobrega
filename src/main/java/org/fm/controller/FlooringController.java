package org.fm.controller;

import org.fm.dao.OrderPersistenceException;
import org.fm.dto.Order;
import org.fm.service.FlooringServiceLayer;
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
        } catch (OrderPersistenceException e) {
            view.displayErrorMessage("No orders for that date");
        }
    }

    private void addOrder() {
        
    }

    private void editOrder() {

    }
    private void removeOrder() {

    }

    private void exportAllData() {

    }
}
