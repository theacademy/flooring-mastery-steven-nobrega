package org.fm.dao;

import org.fm.dto.Product;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductDaoFileImpl implements ProductDao {

    private String productsFile;

    public ProductDaoFileImpl(String productsFile) {
        this.productsFile = productsFile;
    }

    @Override
    public List<Product> getAllProducts() throws OrderPersistenceException {
        List<Product> products = new ArrayList<>();
        try (Scanner sc = new Scanner(new FileReader(productsFile))) {
            if (sc.hasNextLine())
                sc.nextLine(); // skip header
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty())
                    continue;
                String[] productTokens = line.split(",");
                products.add(new Product(productTokens[0], new BigDecimal(productTokens[1]), new BigDecimal(productTokens[2])));
            }
        } catch (FileNotFoundException e) {
            throw new OrderPersistenceException("Products file not found: " + productsFile, e);
        }
        return products;
    }
}
