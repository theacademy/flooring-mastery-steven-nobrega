package org.fm.dao;

import org.fm.dto.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderDao {
    List<Order> getOrdersByDate(LocalDate date) throws OrderPersistenceException;

    Order getOrder(LocalDate date, int orderNumber) throws OrderPersistenceException;

    Order addOrder(LocalDate date, Order order) throws OrderPersistenceException;

    Order editOrder(LocalDate date, Order order) throws OrderPersistenceException;

    Order removeOrder(LocalDate date, int orderNumber) throws OrderPersistenceException;
}
