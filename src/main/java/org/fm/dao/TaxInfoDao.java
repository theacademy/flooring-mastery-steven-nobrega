package org.fm.dao;

import org.fm.dto.TaxInfo;

import java.util.List;

public interface TaxInfoDao {
    List<TaxInfo> getAllTaxes() throws OrderPersistenceException;
}
