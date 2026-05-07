package org.fm.service;

import org.fm.dao.OrderDao;
import org.fm.dao.OrderPersistenceException;
import org.fm.dao.ProductDao;
import org.fm.dao.TaxInfoDao;
import org.fm.dto.Order;
import org.fm.dto.Product;
import org.fm.dto.TaxInfo;

import java.time.LocalDate;
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
    public List<Order> getOrdersByDate(LocalDate date) throws OrderPersistenceException {
        try {
            List<Order> list = orderDao.getOrdersByDate(date);
            if (list == null || list.isEmpty())
                throw new OrderPersistenceException("No orders found for " + date + ".");
            return list;
        } catch (OrderPersistenceException e) {
            throw new OrderPersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public Order addOrder(LocalDate date, Order order) throws OrderPersistenceException {
        return null;
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws OrderPersistenceException, OrderDataValidationException {
        return null;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws OrderPersistenceException {
        return null;
    }

    @Override
    public void exportAllData() throws OrderPersistenceException {

    }

    @Override
    public List<Product> getAllProducts() throws OrderPersistenceException {
        return List.of();
    }

    @Override
    public List<TaxInfo> getAllTaxes() throws OrderPersistenceException {
        return List.of();
    }

    @Override
    public Order calculateOrder(Order order) throws OrderPersistenceException {
        return null;
    }

    @Override
    public int getNextOrderNumber(LocalDate date) throws OrderPersistenceException {
        return 0;
    }
}
