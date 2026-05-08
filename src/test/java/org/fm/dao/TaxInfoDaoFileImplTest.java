package org.fm.dao;

import org.fm.dto.TaxInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaxInfoDaoFileImplTest {
    private TaxInfoDao dao;
    private static final String TEST_TAXES_FILE = "src/test/resources/TestData/Taxes.txt";

    @BeforeEach
    public void setUp() {
        dao = new TaxInfoDaoFileImpl(TEST_TAXES_FILE);
    }

    @Test
    public void testGetAllTaxesReturnsCorrectCount() throws Exception {
        List<TaxInfo> taxes = dao.getAllTaxes();
        assertEquals(4, taxes.size());
    }

    @Test
    public void testGetAllTaxesContainsCalifornia() throws Exception {
        List<TaxInfo> taxes = dao.getAllTaxes();
        TaxInfo ca = taxes.stream()
                .filter(t -> t.getStateAbbreviation().equals("CA"))
                .findFirst()
                .orElse(null);

        assertNotNull(ca);
        assertEquals("California", ca.getStateName());
        assertEquals(new BigDecimal("25.00"), ca.getTaxRate());
    }

    @Test
    public void testGetAllTaxesContainsTexas() throws Exception {
        List<TaxInfo> taxes = dao.getAllTaxes();
        TaxInfo tx = taxes.stream()
                .filter(t -> t.getStateAbbreviation().equals("TX"))
                .findFirst()
                .orElse(null);

        assertNotNull(tx);
        assertEquals("Texas", tx.getStateName());
        assertEquals(new BigDecimal("4.45"), tx.getTaxRate());
    }

    @Test
    public void testGetAllTaxesContainsWashington() throws Exception {
        List<TaxInfo> taxes = dao.getAllTaxes();
        TaxInfo wa = taxes.stream()
                .filter(t -> t.getStateAbbreviation().equals("WA"))
                .findFirst()
                .orElse(null);

        assertNotNull(wa);
        assertEquals("Washington", wa.getStateName());
        assertEquals(new BigDecimal("9.25"), wa.getTaxRate());
    }

    @Test
    public void testGetAllTaxesContainsKentucky() throws Exception {
        List<TaxInfo> taxes = dao.getAllTaxes();
        TaxInfo ky = taxes.stream()
                .filter(t -> t.getStateAbbreviation().equals("KY"))
                .findFirst()
                .orElse(null);

        assertNotNull(ky);
        assertEquals("Kentucky", ky.getStateName());
        assertEquals(new BigDecimal("6.00"), ky.getTaxRate());
    }

    @Test
    public void testGetAllTaxesNoneAreNull() throws Exception {
        List<TaxInfo> taxes = dao.getAllTaxes();
        for (TaxInfo t : taxes) {
            assertNotNull(t.getStateAbbreviation());
            assertNotNull(t.getStateName());
            assertNotNull(t.getTaxRate());
        }
    }

    @Test
    public void testGetAllTaxesFileNotFoundThrows() {
        TaxInfoDao badDao = new TaxInfoDaoFileImpl("nonexistent/path/Taxes.txt");
        assertThrows(OrderPersistenceException.class, () ->
                badDao.getAllTaxes());
    }
}