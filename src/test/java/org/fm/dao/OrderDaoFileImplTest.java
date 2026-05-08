package org.fm.dao;

import org.fm.dto.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderDaoFileImplTest {

    OrderDao testOrderDao;
    private static final LocalDate TEST_DATE = LocalDate.of(2026, 8, 21);

    public OrderDaoFileImplTest() {}

    @BeforeEach
    public void setUp() throws Exception {
        String testFile = "Orders/Orders_08212026.txt";
        new FileWriter(testFile);
        testOrderDao = new OrderDaoFileImpl();
    }

    private Order buildTestOrder(int orderNumber) {
        Order o = new Order(orderNumber);
        o.setCustomerName("Ada Lovelace");
        o.getTaxInfo().setStateAbbreviation("CA");
        o.getTaxInfo().setTaxRate(new BigDecimal("25.00"));
        o.getProduct().setProductType("Tile");
        o.setArea(new BigDecimal("249.00"));
        o.getProduct().setCostPerSquareFoot(new BigDecimal("3.50"));
        o.getProduct().setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        o.setMaterialCost(new BigDecimal("871.50"));
        o.setLaborCost(new BigDecimal("1033.35"));
        o.setTax(new BigDecimal("476.21"));
        o.setTotal(new BigDecimal("2381.06"));
        return o;
    }

    @Test
    public void testAddAndGetOrder() throws Exception {
        Order order = buildTestOrder(testOrderDao.getMaxOrderNumber());
        testOrderDao.addOrder(TEST_DATE, order);

        List<Order> orders = testOrderDao.getOrdersByDate(TEST_DATE);
        assertEquals(1, orders.size());
        assertEquals("Ada Lovelace", orders.get(0).getCustomerName());
    }

    @Test
    public void testGetOrdersByDateNoFileReturnsEmpty() throws Exception {
        List<Order> orders = testOrderDao.getOrdersByDate(LocalDate.of(2099, 1, 1));
        assertTrue(orders.isEmpty());
    }

    @Test
    public void testEditOrder() throws Exception {
        Order order = buildTestOrder(1);
        testOrderDao.addOrder(TEST_DATE, order);

        order.setCustomerName("Charles Babbage");
        testOrderDao.editOrder(TEST_DATE, order);

        List<Order> orders = testOrderDao.getOrdersByDate(TEST_DATE);
        assertEquals("Charles Babbage", orders.get(0).getCustomerName());
    }

    @Test
    public void testRemoveOrder() throws Exception {
        Order order = buildTestOrder(1);
        testOrderDao.addOrder(TEST_DATE, order);

        testOrderDao.removeOrder(TEST_DATE, 1);

        List<Order> orders = testOrderDao.getOrdersByDate(TEST_DATE);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void testRemoveOrderNotFoundThrows() {
        assertThrows(OrderPersistenceException.class, () ->
                testOrderDao.removeOrder(TEST_DATE, 999));
    }

    @Test
    public void testGetMaxOrderNumberAcrossMultipleDates() throws Exception {
        testOrderDao.addOrder(TEST_DATE, buildTestOrder(testOrderDao.getMaxOrderNumber()));
        testOrderDao.addOrder(LocalDate.of(2026, 9, 15), buildTestOrder(testOrderDao.getMaxOrderNumber()));

        int max = testOrderDao.getMaxOrderNumber();
        assertEquals(6, max);

        // clean up second test file
        new File("Orders/Orders_09152026.txt").delete();
    }

}