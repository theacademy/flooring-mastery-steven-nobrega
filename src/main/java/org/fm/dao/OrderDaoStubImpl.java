package org.fm.dao;

import org.fm.dto.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoStubImpl implements OrderDao{
    public static final LocalDate TEST_DATE = LocalDate.of(2026, 7, 1);
    public static final int TEST_ORDER_NUMBER = 1;

    private Order testOrder;

    public OrderDaoStubImpl() {
        testOrder = new Order(TEST_ORDER_NUMBER);
        testOrder.setCustomerName("Ada Lovelace");
        testOrder.getTaxInfo().setStateAbbreviation("CA");
        testOrder.getTaxInfo().setTaxRate(new BigDecimal("25.00"));
        testOrder.getProduct().setProductType("Tile");
        testOrder.setArea(new BigDecimal("249.00"));
        testOrder.getProduct().setCostPerSquareFoot(new BigDecimal("3.50"));
        testOrder.getProduct().setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        testOrder.setMaterialCost(new BigDecimal("871.50"));
        testOrder.setLaborCost(new BigDecimal("1033.35"));
        testOrder.setTax(new BigDecimal("476.21"));
        testOrder.setTotal(new BigDecimal("2381.06"));
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws OrderPersistenceException {
        if (date.equals(TEST_DATE)) {
            List<Order> orders = new ArrayList<>();
            orders.add(testOrder);
            return orders;
        }
        return new ArrayList<>();
    }

    @Override
    public Order addOrder(LocalDate date, Order order) throws OrderPersistenceException {
        return order;
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws OrderPersistenceException {
        return order;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws OrderPersistenceException {
        if (date.equals(TEST_DATE) && orderNumber == TEST_ORDER_NUMBER) {
            return testOrder;
        }
        throw new OrderPersistenceException("Order not found.");
    }

    @Override
    public void exportAllData() throws OrderPersistenceException {

    }

    @Override
    public int getMaxOrderNumber() throws OrderPersistenceException {
        return TEST_ORDER_NUMBER;
    }
}
