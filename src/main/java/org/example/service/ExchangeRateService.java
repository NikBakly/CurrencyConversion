package org.example.service;

import org.example.dao.ExchangeRateDao;
import org.example.dao.ExchangeRateDaoImpl;
import org.example.dto.ExchangeDto;
import org.example.dto.ExchangeRateDto;
import org.example.model.ExchangeRate;
import org.example.model.exception.ConflictException;
import org.example.model.exception.InternalServerErrorException;
import org.example.model.exception.NotFoundException;

import java.util.List;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDaoImpl.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public void create(ExchangeRateDto exchangeRateDto) throws ConflictException, InternalServerErrorException {
        exchangeRateDao.create(exchangeRateDto);
    }

    public void updateById(Integer id, ExchangeRateDto exchangeRateDto)
            throws ConflictException, InternalServerErrorException, NotFoundException {
        exchangeRateDao.updateById(id, exchangeRateDto);
    }

    public ExchangeRate getByCodes(String codes) throws NotFoundException, InternalServerErrorException {
        return exchangeRateDao.getByCodes(codes);
    }

    public ExchangeRate getById(Integer id) throws NotFoundException, InternalServerErrorException {
        return exchangeRateDao.getById(id);
    }

    public List<ExchangeRate> getAll() throws InternalServerErrorException {
        return exchangeRateDao.getAll();
    }

    public void deleteById(Integer id) throws InternalServerErrorException, NotFoundException {
        exchangeRateDao.deleteById(id);
    }

    public void deleteByCurrencyId(Integer id) {
        exchangeRateDao.deleteByCurrencyId(id);
    }

    public ExchangeDto convertCurrency(String fromCurrencyCode,
                                       String toCurrencyCode,
                                       Double amount) throws NotFoundException, InternalServerErrorException {
        String codes = fromCurrencyCode.trim().toUpperCase() + toCurrencyCode.trim().toUpperCase();
        ExchangeRate foundExchangeRate = exchangeRateDao.getByCodes(codes);
        Double convertedAmount = amount * foundExchangeRate.getRate();
        return new ExchangeDto(
                foundExchangeRate,
                amount,
                convertedAmount
        );
    }
}
