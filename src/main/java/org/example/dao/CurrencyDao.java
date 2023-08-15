package org.example.dao;

import org.example.dto.CurrencyDto;
import org.example.model.Currency;

import java.util.List;

public interface CurrencyDao {
    void create(CurrencyDto currencyDto);

    void updateById(Integer id, CurrencyDto currencyDto);

    Currency getCurrencyByCode(String code);

    Currency getCurrencyById(Integer id);

    List<Currency> getAll();

    void deleteById(Integer id);

}
