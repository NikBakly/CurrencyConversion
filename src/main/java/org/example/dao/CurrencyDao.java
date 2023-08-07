package org.example.dao;

import org.example.model.Currency;

import java.util.List;

public interface CurrencyDao {
    void create(Currency currency);

    void updateById(Integer id, Currency currency);

    Currency getByCode(String code);

    List<Currency> getAll();

    void deleteById(Integer id);

}
