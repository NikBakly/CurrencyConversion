package org.example.dao;

import org.example.model.ExchangeRate;

import java.util.List;

public interface ExchangeRateDao {
    void create(ExchangeRate exchangeRate);

    void updateById(Integer id, ExchangeRate exchangeRate);

    ExchangeRate getByCode(String code);

    List<ExchangeRate> getAll();

    void deleteById(Integer id);
}
