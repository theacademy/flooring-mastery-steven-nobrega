package org.fm.service;

import org.fm.dao.OrderPersistenceException;
import org.fm.dto.Order;
import org.fm.dto.Product;
import org.fm.dto.TaxInfo;

import java.time.LocalDate;
import java.util.List;

public interface FlooringServiceLayer {
    List<Order> getOrdersByDate(LocalDate date) throws OrderPersistenceException, NoOrdersOnDateException;

    Order addOrder(LocalDate date, Order order) throws
            OrderPersistenceException,
            NoOrdersOnDateException,
            OrderDataValidationException;

    Order editOrder(LocalDate date, Order order) throws
            OrderPersistenceException,
            NoOrdersOnDateException,
            OrderDataValidationException;

    Order removeOrder(LocalDate date, int orderNumber) throws
            OrderPersistenceException,
            NoOrdersOnDateException,
            OrderDataValidationException;

    void exportAllData() throws OrderPersistenceException;

    List<Product> getAllProducts()
            throws OrderPersistenceException;

    List<TaxInfo> getAllTaxes()
            throws OrderPersistenceException;

    Order calculateOrder(Order order) throws OrderPersistenceException, OrderDataValidationException;

    int getNextOrderNumber() throws OrderPersistenceException;
}
