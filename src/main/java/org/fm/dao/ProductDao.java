package org.fm.dao;

import org.fm.dto.Product;

import java.util.List;

public interface ProductDao {
    Product getProduct(String productType) throws OrderPersistenceException;

    List<Product> getAllProducts() throws OrderPersistenceException;
}
