package org.fm.dao;

import org.fm.dto.TaxInfo;

import java.util.List;

public interface TaxInfoDao {
    TaxInfo getTax(String stateAbbreviation) throws OrderPersistenceException;

    List<TaxInfo> getAllTaxes() throws OrderPersistenceException;
}
