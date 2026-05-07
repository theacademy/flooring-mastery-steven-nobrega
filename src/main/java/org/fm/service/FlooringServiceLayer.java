package org.fm.service;

import org.fm.dao.OrderPersistenceException;
import org.fm.dto.Order;
import org.fm.dto.Product;
import org.fm.dto.TaxInfo;

import java.time.LocalDate;
import java.util.List;

public interface FlooringServiceLayer {
    List<Order> getOrdersByDate(LocalDate date) throws OrderPersistenceException;

    Order addOrder(LocalDate date, Order order) throws
            OrderPersistenceException,
            OrderDuplicateIdException,
            OrderDataValidationException;

    Order editOrder(LocalDate date, Order order) throws
            OrderPersistenceException,
            OrderDataValidationException;

    Order removeOrder(LocalDate date, int orderNumber) throws OrderPersistenceException;

    void exportAllData() throws OrderPersistenceException;

    List<Product> getAllProducts()
            throws OrderPersistenceException;

    List<TaxInfo> getAllTaxes()
            throws OrderPersistenceException;

    Order calculateOrder(Order order) throws OrderPersistenceException;

    int getNextOrderNumber(LocalDate date) throws OrderPersistenceException;
}
