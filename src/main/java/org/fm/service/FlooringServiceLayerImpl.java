package org.fm.service;

import org.fm.dao.*;
import org.fm.dto.Order;
import org.fm.dto.Product;
import org.fm.dto.TaxInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlooringServiceLayerImpl implements FlooringServiceLayer {
    private OrderDao orderDao;
    private ProductDao productDao;
    private TaxInfoDao taxInfoDao;

    public FlooringServiceLayerImpl(OrderDao orderDao, ProductDao productDao, TaxInfoDao taxInfoDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxInfoDao = taxInfoDao;
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws OrderPersistenceException, NoOrdersOnDateException {
        try {
            List<Order> list = orderDao.getOrdersByDate(date);
            if (list == null || list.isEmpty())
                throw new NoOrdersOnDateException("No orders found for " + date + ".");
            return list;
        } catch (OrderPersistenceException e) {
            throw new OrderPersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public Order addOrder(LocalDate date, Order order) throws OrderPersistenceException, OrderDataValidationException {
        validateOrder(order);
        // Do order calculations (material cost, labor, tax, total...)
        order = calculateOrder(order);
        order.setOrderNumber(getNextOrderNumber());
        try {
            return orderDao.addOrder(date, order);
        } catch (OrderPersistenceException e) {
            throw new OrderPersistenceException(e.getMessage(), e);
        }
    }


    @Override
    public Order editOrder(LocalDate date, Order order) throws OrderPersistenceException, OrderDataValidationException {
        validateOrder(order);
        order = calculateOrder(order);
        try {
            return orderDao.editOrder(date, order);
        } catch (OrderPersistenceException e) {
            throw new OrderPersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws OrderPersistenceException, OrderDataValidationException {
        try {
            Order orderToremove = orderDao.removeOrder(date, orderNumber);
            if (orderToremove == null)
                throw new OrderDataValidationException("Order #" + orderNumber + " not found.");
            return orderToremove;
        } catch (OrderPersistenceException e) {
            throw new OrderPersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public void exportAllData() throws OrderPersistenceException {
        try {
            orderDao.exportAllData();
        } catch (OrderPersistenceException e) {
            throw new OrderPersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Product> getAllProducts() throws OrderPersistenceException {
        try {
            return productDao.getAllProducts();
        } catch (OrderPersistenceException e) {
            throw new OrderPersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public List<TaxInfo> getAllTaxes() throws OrderPersistenceException {
        try {
            return taxInfoDao.getAllTaxes();
        } catch (OrderPersistenceException e) {
            throw new OrderPersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public Order calculateOrder(Order order) throws OrderPersistenceException, OrderDataValidationException {
        // pull matching product from productDao
        List<Product> products = getAllProducts();
        Product matchedProduct = products.stream()
                .filter(p -> p.getProductType().equalsIgnoreCase(order.getProduct().getProductType()))
                .findFirst()
                .orElseThrow(() -> new OrderDataValidationException(
                        "Product type not found: " + order.getProduct().getProductType()));

        // pull matching tax rate from taxInfoDao
        List<TaxInfo> taxes = getAllTaxes();
        TaxInfo matchedTax = taxes.stream()
                .filter(t -> t.getStateAbbreviation().equalsIgnoreCase(order.getTaxInfo().getStateAbbreviation()))
                .findFirst()
                .orElseThrow(() -> new OrderDataValidationException(
                        "State not found: " + order.getTaxInfo().getStateAbbreviation()));

        // add rates to the order
        order.getProduct().setCostPerSquareFoot(matchedProduct.getCostPerSquareFoot());
        order.getProduct().setLaborCostPerSquareFoot(matchedProduct.getLaborCostPerSquareFoot());
        order.getTaxInfo().setTaxRate(matchedTax.getTaxRate());

        // run the four calculations
        BigDecimal materialCost = order.getArea()
                .multiply(order.getProduct().getCostPerSquareFoot())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal laborCost = order.getArea()
                .multiply(order.getProduct().getLaborCostPerSquareFoot())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal tax = materialCost.add(laborCost)
                .multiply(order.getTaxInfo().getTaxRate()
                        .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = materialCost.add(laborCost).add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        order.setMaterialCost(materialCost);
        order.setLaborCost(laborCost);
        order.setTax(tax);
        order.setTotal(total);

        return order;
    }

    @Override
    public int getNextOrderNumber() throws OrderPersistenceException {
        try {
            return orderDao.getMaxOrderNumber() + 1;
        } catch (OrderPersistenceException e) {
            throw new OrderPersistenceException(e.getMessage(), e);
        }
    }

    private void validateOrder(Order order) throws OrderDataValidationException {
        if (order.getCustomerName() == null || order.getCustomerName().isBlank())
            throw new OrderDataValidationException("Customer name may not be blank.");
        if (!order.getCustomerName().matches("[a-zA-Z0-9., ]+"))
            throw new OrderDataValidationException("Customer name contains invalid characters.");
        if (order.getTaxInfo().getStateAbbreviation() == null || order.getTaxInfo().getStateAbbreviation().isBlank())
            throw new OrderDataValidationException("State may not be blank.");
        if (order.getProduct().getProductType() == null || order.getProduct().getProductType().isBlank())
            throw new OrderDataValidationException("Product type may not be blank.");
        if (order.getArea() == null || order.getArea().compareTo(new BigDecimal("100")) < 0)
            throw new OrderDataValidationException("Area must be at least 100 sq ft.");
    }
}
