package org.fm.dao;

import org.fm.dto.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoStubImpl implements ProductDao {
    @Override
    public List<Product> getAllProducts() throws OrderPersistenceException {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Tile", new BigDecimal("3.50"), new BigDecimal("4.15")));
        products.add(new Product("Wood", new BigDecimal("5.15"), new BigDecimal("4.75")));
        return products;
    }
}
