package org.fm.dao;

import org.fm.dto.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDaoFileImplTest {

    private ProductDao dao;
    private static final String TEST_PRODUCTS_FILE = "src/test/resources/TestData/Products.txt";

    @BeforeEach
    public void setUp() {
        dao = new ProductDaoFileImpl(TEST_PRODUCTS_FILE);
    }

    @Test
    public void testGetAllProductsReturnsCorrectCount() throws Exception {
        List<Product> products = dao.getAllProducts();
        assertEquals(4, products.size());
    }

    @Test
    public void testGetAllProductsContainsTile() throws Exception {
        List<Product> products = dao.getAllProducts();
        Product tile = products.stream()
                .filter(p -> p.getProductType().equals("Tile"))
                .findFirst()
                .orElse(null);

        assertNotNull(tile);
        assertEquals(new BigDecimal("3.50"), tile.getCostPerSquareFoot());
        assertEquals(new BigDecimal("4.15"), tile.getLaborCostPerSquareFoot());
    }

    @Test
    public void testGetAllProductsContainsWood() throws Exception {
        List<Product> products = dao.getAllProducts();
        Product wood = products.stream()
                .filter(p -> p.getProductType().equals("Wood"))
                .findFirst()
                .orElse(null);

        assertNotNull(wood);
        assertEquals(new BigDecimal("5.15"), wood.getCostPerSquareFoot());
        assertEquals(new BigDecimal("4.75"), wood.getLaborCostPerSquareFoot());
    }

    @Test
    public void testGetAllProductsContainsCarpet() throws Exception {
        List<Product> products = dao.getAllProducts();
        Product carpet = products.stream()
                .filter(p -> p.getProductType().equals("Carpet"))
                .findFirst()
                .orElse(null);

        assertNotNull(carpet);
        assertEquals(new BigDecimal("2.25"), carpet.getCostPerSquareFoot());
        assertEquals(new BigDecimal("2.10"), carpet.getLaborCostPerSquareFoot());
    }

    @Test
    public void testGetAllProductsContainsLaminate() throws Exception {
        List<Product> products = dao.getAllProducts();
        Product laminate = products.stream()
                .filter(p -> p.getProductType().equals("Laminate"))
                .findFirst()
                .orElse(null);

        assertNotNull(laminate);
        assertEquals(new BigDecimal("1.75"), laminate.getCostPerSquareFoot());
        assertEquals(new BigDecimal("2.10"), laminate.getLaborCostPerSquareFoot());
    }

    @Test
    public void testGetAllProductsNoneAreNull() throws Exception {
        List<Product> products = dao.getAllProducts();
        for (Product p : products) {
            assertNotNull(p.getProductType());
            assertNotNull(p.getCostPerSquareFoot());
            assertNotNull(p.getLaborCostPerSquareFoot());
        }
    }

    @Test
    public void testGetAllProductsFileNotFoundThrows() {
        ProductDao badDao = new ProductDaoFileImpl("nonexistent/path/Products.txt");
        assertThrows(OrderPersistenceException.class, () ->
                badDao.getAllProducts());
    }
}