package org.example.dao;

import org.example.dto.ExchangeRateDto;
import org.example.model.ExchangeRate;

import java.util.List;

public interface ExchangeRateDao {
    void create(ExchangeRateDto exchangeRateDto);

    void updateById(Integer id, ExchangeRateDto exchangeRateDto);

    ExchangeRate getByCodes(String codes);

    ExchangeRate getById(Integer id);

    List<ExchangeRate> getAll();

    void deleteById(Integer id);

    void deleteByCurrencyId(Integer id);
}
