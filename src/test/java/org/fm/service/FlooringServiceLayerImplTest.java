package org.fm.service;

import org.fm.dao.OrderDaoStubImpl;
import org.fm.dao.OrderPersistenceException;
import org.fm.dto.Order;
import org.fm.dto.Product;
import org.fm.dto.TaxInfo;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlooringServiceLayerImplTest {
    private FlooringServiceLayer service;

    public FlooringServiceLayerImplTest() {
        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        service =
                ctx.getBean("serviceLayer", FlooringServiceLayer.class);
    }

    // ---- getOrdersByDate ----

    @Test
    public void testGetOrdersByDateSuccess() throws Exception {
        List<Order> orders = service.getOrdersByDate(OrderDaoStubImpl.TEST_DATE);
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals("Ada Lovelace", orders.get(0).getCustomerName());
    }

    @Test
    public void testGetOrdersByDateNoOrders() {
        assertThrows(NoOrdersOnDateException.class, () ->
                service.getOrdersByDate(LocalDate.of(2099, 1, 1)));
    }

    // ---- addOrder ----

    @Test
    public void testAddOrderSuccess() throws Exception {
        Order order = new Order(0);
        order.setCustomerName("Test User");
        order.getTaxInfo().setStateAbbreviation("CA");
        order.getProduct().setProductType("Tile");
        order.setArea(new BigDecimal("150.00"));

        Order added = service.addOrder(OrderDaoStubImpl.TEST_DATE, order);
        assertNotNull(added);
        assertEquals("Test User", added.getCustomerName());
        assertNotNull(added.getMaterialCost());
        assertNotNull(added.getLaborCost());
        assertNotNull(added.getTax());
        assertNotNull(added.getTotal());
    }

    @Test
    public void testAddOrderBlankNameFails() {
        Order order = new Order(0);
        order.setCustomerName("");
        order.getTaxInfo().setStateAbbreviation("CA");
        order.getProduct().setProductType("Tile");
        order.setArea(new BigDecimal("150.00"));

        assertThrows(OrderDataValidationException.class, () ->
                service.addOrder(OrderDaoStubImpl.TEST_DATE, order));
    }

    @Test
    public void testAddOrderInvalidNameCharactersFails() {
        Order order = new Order(0);
        order.setCustomerName("Ada@Lovelace!");
        order.getTaxInfo().setStateAbbreviation("CA");
        order.getProduct().setProductType("Tile");
        order.setArea(new BigDecimal("150.00"));

        assertThrows(OrderDataValidationException.class, () ->
                service.addOrder(OrderDaoStubImpl.TEST_DATE, order));
    }

    @Test
    public void testAddOrderInvalidStateFails() {
        Order order = new Order(0);
        order.setCustomerName("Test User");
        order.getTaxInfo().setStateAbbreviation("XX");
        order.getProduct().setProductType("Tile");
        order.setArea(new BigDecimal("150.00"));

        assertThrows(OrderDataValidationException.class, () ->
                service.addOrder(OrderDaoStubImpl.TEST_DATE, order));
    }

    @Test
    public void testAddOrderInvalidProductFails() {
        Order order = new Order(0);
        order.setCustomerName("Test User");
        order.getTaxInfo().setStateAbbreviation("CA");
        order.getProduct().setProductType("Carpet");
        order.setArea(new BigDecimal("150.00"));

        assertThrows(OrderDataValidationException.class, () ->
                service.addOrder(OrderDaoStubImpl.TEST_DATE, order));
    }

    @Test
    public void testAddOrderAreaTooSmallFails() {
        Order order = new Order(0);
        order.setCustomerName("Test User");
        order.getTaxInfo().setStateAbbreviation("CA");
        order.getProduct().setProductType("Tile");
        order.setArea(new BigDecimal("99.00"));

        assertThrows(OrderDataValidationException.class, () ->
                service.addOrder(OrderDaoStubImpl.TEST_DATE, order));
    }

    // ---- editOrder ----

@Test
public void testEditOrderSuccess() throws Exception {
    Order order = new Order(OrderDaoStubImpl.TEST_ORDER_NUMBER);
    order.setCustomerName("Charles Babbage");
    order.getTaxInfo().setStateAbbreviation("CA");
    order.getProduct().setProductType("Tile");
    order.setArea(new BigDecimal("249.00"));

    Order edited = service.editOrder(OrderDaoStubImpl.TEST_DATE, order);
    assertNotNull(edited);
    assertEquals("Charles Babbage", edited.getCustomerName());
}

@Test
public void testEditOrderRecalculatesCorrectly() throws Exception {
    Order order = new Order(OrderDaoStubImpl.TEST_ORDER_NUMBER);
    order.setCustomerName("Charles Babbage");
    order.getTaxInfo().setStateAbbreviation("TX");
    order.getProduct().setProductType("Wood");
    order.setArea(new BigDecimal("300.00"));

    Order edited = service.editOrder(OrderDaoStubImpl.TEST_DATE, order);

    // materialCost = 300.00 * 5.15 = 1545.00
    assertEquals(new BigDecimal("1545.00"), edited.getMaterialCost());
    // laborCost = 300.00 * 4.75 = 1425.00
    assertEquals(new BigDecimal("1425.00"), edited.getLaborCost());
    // tax = (1545.00 + 1425.00) * (4.45 / 100) = 132.17
    assertEquals(new BigDecimal("132.17"), edited.getTax());
    // total = 1545.00 + 1425.00 + 132.17 = 3102.17
    assertEquals(new BigDecimal("3102.17"), edited.getTotal());
}

@Test
public void testEditOrderBlankNameFails() {
    Order order = new Order(OrderDaoStubImpl.TEST_ORDER_NUMBER);
    order.setCustomerName("");
    order.getTaxInfo().setStateAbbreviation("CA");
    order.getProduct().setProductType("Tile");
    order.setArea(new BigDecimal("249.00"));

    assertThrows(OrderDataValidationException.class, () ->
            service.editOrder(OrderDaoStubImpl.TEST_DATE, order));
}

@Test
public void testEditOrderInvalidNameCharactersFails() {
    Order order = new Order(OrderDaoStubImpl.TEST_ORDER_NUMBER);
    order.setCustomerName("Charles@Babbage!");
    order.getTaxInfo().setStateAbbreviation("CA");
    order.getProduct().setProductType("Tile");
    order.setArea(new BigDecimal("249.00"));

    assertThrows(OrderDataValidationException.class, () ->
            service.editOrder(OrderDaoStubImpl.TEST_DATE, order));
}

@Test
public void testEditOrderInvalidStateFails() {
    Order order = new Order(OrderDaoStubImpl.TEST_ORDER_NUMBER);
    order.setCustomerName("Charles Babbage");
    order.getTaxInfo().setStateAbbreviation("XX");
    order.getProduct().setProductType("Tile");
    order.setArea(new BigDecimal("249.00"));

    assertThrows(OrderDataValidationException.class, () ->
            service.editOrder(OrderDaoStubImpl.TEST_DATE, order));
}

@Test
public void testEditOrderInvalidProductFails() {
    Order order = new Order(OrderDaoStubImpl.TEST_ORDER_NUMBER);
    order.setCustomerName("Charles Babbage");
    order.getTaxInfo().setStateAbbreviation("CA");
    order.getProduct().setProductType("Carpet");
    order.setArea(new BigDecimal("249.00"));

    assertThrows(OrderDataValidationException.class, () ->
            service.editOrder(OrderDaoStubImpl.TEST_DATE, order));
}

@Test
public void testEditOrderAreaTooSmallFails() {
    Order order = new Order(OrderDaoStubImpl.TEST_ORDER_NUMBER);
    order.setCustomerName("Charles Babbage");
    order.getTaxInfo().setStateAbbreviation("CA");
    order.getProduct().setProductType("Tile");
    order.setArea(new BigDecimal("50.00"));

    assertThrows(OrderDataValidationException.class, () ->
            service.editOrder(OrderDaoStubImpl.TEST_DATE, order));
}

    // ---- calculateOrder ----

    @Test
    public void testCalculateOrderCorrectMaterialCost() throws Exception {
        Order order = new Order(0);
        order.setCustomerName("Test User");
        order.getTaxInfo().setStateAbbreviation("CA");
        order.getProduct().setProductType("Tile");
        order.setArea(new BigDecimal("249.00"));

        Order calculated = service.calculateOrder(order);

        // materialCost = 249.00 * 3.50 = 871.50
        assertEquals(new BigDecimal("871.50"), calculated.getMaterialCost());
    }

    @Test
    public void testCalculateOrderCorrectLaborCost() throws Exception {
        Order order = new Order(0);
        order.setCustomerName("Test User");
        order.getTaxInfo().setStateAbbreviation("CA");
        order.getProduct().setProductType("Tile");
        order.setArea(new BigDecimal("249.00"));

        Order calculated = service.calculateOrder(order);

        // laborCost = 249.00 * 4.15 = 1033.35
        assertEquals(new BigDecimal("1033.35"), calculated.getLaborCost());
    }

    @Test
    public void testCalculateOrderCorrectTax() throws Exception {
        Order order = new Order(0);
        order.setCustomerName("Test User");
        order.getTaxInfo().setStateAbbreviation("CA");
        order.getProduct().setProductType("Tile");
        order.setArea(new BigDecimal("249.00"));

        Order calculated = service.calculateOrder(order);

        // tax = (871.50 + 1033.35) * (25.00 / 100) = 476.21
        assertEquals(new BigDecimal("476.21"), calculated.getTax());
    }

    @Test
    public void testCalculateOrderCorrectTotal() throws Exception {
        Order order = new Order(0);
        order.setCustomerName("Test User");
        order.getTaxInfo().setStateAbbreviation("CA");
        order.getProduct().setProductType("Tile");
        order.setArea(new BigDecimal("249.00"));

        Order calculated = service.calculateOrder(order);

        // total = 871.50 + 1033.35 + 476.21 = 2381.06
        assertEquals(new BigDecimal("2381.06"), calculated.getTotal());
    }

    // ---- removeOrder ----

    @Test
    public void testRemoveOrderSuccess() throws Exception {
        Order removed = service.removeOrder(
                OrderDaoStubImpl.TEST_DATE,
                OrderDaoStubImpl.TEST_ORDER_NUMBER);
        assertNotNull(removed);
        assertEquals(OrderDaoStubImpl.TEST_ORDER_NUMBER, removed.getOrderNumber());
    }

    @Test
    public void testRemoveOrderNotFoundFails() {
        assertThrows(OrderPersistenceException.class, () ->
                service.removeOrder(OrderDaoStubImpl.TEST_DATE, 999));
    }

    // ---- getAllProducts ----

    @Test
    public void testGetAllProductsReturnsCorrectCount() throws Exception {
        List<Product> products = service.getAllProducts();
        assertEquals(2, products.size());
    }

    // ---- getAllTaxes ----

    @Test
    public void testGetAllTaxesReturnsCorrectCount() throws Exception {
        List<TaxInfo> taxes = service.getAllTaxes();
        assertEquals(2, taxes.size());
    }

    // ---- getNextOrderNumber ----

    @Test
    public void testGetNextOrderNumberIsGlobalMax() throws Exception {
        // stub returns max of 1 so next should be 2
        int next = service.getNextOrderNumber();
        assertEquals(2, next);
    }
}