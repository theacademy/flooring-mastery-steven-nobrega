package org.fm.dao;

import org.fm.dto.Product;

import java.util.List;

public interface ProductDao {
    List<Product> getAllProducts() throws OrderPersistenceException;
}
