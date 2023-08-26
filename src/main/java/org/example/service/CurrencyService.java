package org.example.service;

import org.example.dao.CurrencyDao;
import org.example.dao.CurrencyDaoImpl;
import org.example.dto.CurrencyDto;
import org.example.model.Currency;
import org.example.model.exception.InternalServerErrorException;
import org.example.model.exception.NotFoundException;

import java.util.List;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyDao currencyDao = CurrencyDaoImpl.getInstance();
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public void create(CurrencyDto currencyDto) throws InternalServerErrorException {
        currencyDao.create(currencyDto);
    }

    public void updateById(Integer id, CurrencyDto currencyDto) throws InternalServerErrorException {
        currencyDao.updateById(id, currencyDto);
    }

    public Currency getCurrencyByCode(String code) throws NotFoundException, InternalServerErrorException {
        return currencyDao.getCurrencyByCode(code);
    }

    public Currency getCurrencyById(Integer id) throws NotFoundException, InternalServerErrorException {
        return currencyDao.getCurrencyById(id);
    }

    public List<Currency> getAll() throws InternalServerErrorException {
        return currencyDao.getAll();
    }

    public void deleteById(Integer id) throws NotFoundException, InternalServerErrorException {
        currencyDao.deleteById(id);
        exchangeRateService.deleteByCurrencyId(id);
    }

}
