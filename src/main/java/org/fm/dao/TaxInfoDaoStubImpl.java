package org.fm.dao;

import org.fm.dto.TaxInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TaxInfoDaoStubImpl implements TaxInfoDao{
    @Override
    public List<TaxInfo> getAllTaxes() throws OrderPersistenceException {
        List<TaxInfo> taxes = new ArrayList<>();
        taxes.add(new TaxInfo("CA", "California", new BigDecimal("25.00")));
        taxes.add(new TaxInfo("TX", "Texas", new BigDecimal("4.45")));
        return taxes;
    }
}
