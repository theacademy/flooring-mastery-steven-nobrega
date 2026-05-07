package org.fm.dao;

import org.fm.dto.TaxInfo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaxInfoDaoFileImpl implements TaxInfoDao {

    private String taxesFile;

    public TaxInfoDaoFileImpl(String taxesFile) {
        this.taxesFile = taxesFile;
    }

    @Override
    public List<TaxInfo> getAllTaxes() throws OrderPersistenceException {
        List<TaxInfo> taxes = new ArrayList<>();
        try (Scanner sc = new Scanner(new FileReader(taxesFile))) {
            if (sc.hasNextLine()) sc.nextLine(); // skip header
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                taxes.add(new TaxInfo(parts[0], parts[1], new BigDecimal(parts[2])));
            }
        } catch (FileNotFoundException e) {
            throw new OrderPersistenceException("Taxes file not found: " + taxesFile, e);
        }
        return taxes;
    }

    public static void main(String[] args) throws OrderPersistenceException{
        TaxInfoDao t = new TaxInfoDaoFileImpl("src/main/resources/Data/Taxes.txt");
        List<TaxInfo> taxes = t.getAllTaxes();
        System.out.println(taxes);
    }
}
