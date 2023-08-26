package org.example.dao;

import org.example.DatabaseConnector;
import org.example.dto.ExchangeRateDto;
import org.example.model.Currency;
import org.example.model.ExchangeRate;
import org.example.model.exception.ConflictException;
import org.example.model.exception.InternalServerErrorException;
import org.example.model.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDaoImpl implements ExchangeRateDao {
    private static final ExchangeRateDaoImpl INSTANCE = new ExchangeRateDaoImpl();
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateDaoImpl.class);

    private final CurrencyDao currencyDao = CurrencyDaoImpl.getInstance();

    private ExchangeRateDaoImpl() {
    }

    public static ExchangeRateDaoImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void create(ExchangeRateDto exchangeRateDto) throws ConflictException, InternalServerErrorException {
        int baseCurrencyId = exchangeRateDto.getBaseCurrency().getId();
        int targetCurrencyId = exchangeRateDto.getTargetCurrency().getId();

        try {
            currencyDao.getCurrencyById(baseCurrencyId);
            currencyDao.getCurrencyById(targetCurrencyId);
        } catch (NotFoundException e) {
            throw new ConflictException(e);
        }

        String insertQuery = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            preparedStatement.setDouble(3, exchangeRateDto.getRate());

            int rowsAffected = preparedStatement.executeUpdate();
            logger.debug(rowsAffected > 0 ? "Currency saved successfully" : "Failed to save currency");

        } catch (SQLException | ClassNotFoundException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Override
    public void updateById(Integer id, ExchangeRateDto exchangeRateDto)
            throws ConflictException, InternalServerErrorException, NotFoundException {
        int baseCurrencyId = exchangeRateDto.getBaseCurrency().getId();
        int targetCurrencyId = exchangeRateDto.getTargetCurrency().getId();

        getById(id);

        try {
            currencyDao.getCurrencyById(baseCurrencyId);
            currencyDao.getCurrencyById(targetCurrencyId);
        } catch (NotFoundException e) {
            throw new ConflictException(e);
        }

        String updateQuery =
                "UPDATE exchange_rates set base_currency_id = ?, target_currency_id = ?, rate = ? WHERE id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            preparedStatement.setDouble(3, exchangeRateDto.getRate());
            preparedStatement.setInt(4, id);

            int rowsAffected = preparedStatement.executeUpdate();
            logger.debug(rowsAffected > 0 ? "ExchangeRate updated successfully" : "Failed to update exchangeRate");
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error when updating exchangeRate");
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public ExchangeRate getByCodes(String codes) throws NotFoundException, InternalServerErrorException {
        String baseCurrencyCode = codes.substring(0, 3);
        String targetCurrencyCode = codes.substring(3);
        Currency baseCurrency;
        Currency targetCurrency;
        baseCurrency = currencyDao.getCurrencyByCode(baseCurrencyCode);
        targetCurrency = currencyDao.getCurrencyByCode(targetCurrencyCode);

        String getByCodesQuery = "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";
        ExchangeRate foundExchangeRate;

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getByCodesQuery)) {
            preparedStatement.setInt(1, baseCurrency.getId());
            preparedStatement.setInt(2, targetCurrency.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                foundExchangeRate = new ExchangeRate(
                        resultSet.getInt("id"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getDouble("rate")
                );
                logger.debug("ExchangeRate with codes={} successfully found", codes);
            } else {
                logger.debug("ExchangeRate with codes={} not found", codes);
                throw new NotFoundException(String.format("ExchangeRate with codes=%s not found", codes));
            }

            return foundExchangeRate;
        } catch (SQLException | ClassNotFoundException e) {
            logger.debug("Error when get exchange by codes");
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public ExchangeRate getById(Integer id) throws NotFoundException, InternalServerErrorException {
        String getByIdQuery = "SELECT * FROM exchange_rates WHERE id = ?";
        ExchangeRate foundExchangeRate;

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getByIdQuery)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Currency baseCurrency = currencyDao
                        .getCurrencyById(resultSet.getInt("base_currency_id"));
                Currency targetCurrency = currencyDao
                        .getCurrencyById(resultSet.getInt("target_currency_id"));
                foundExchangeRate = new ExchangeRate(
                        id,
                        baseCurrency,
                        targetCurrency,
                        resultSet.getDouble("rate"));
                logger.debug("ExchangeRate with id={} successfully found", id);
            } else {
                logger.debug("ExchangeRate with id={} not found", id);
                throw new NotFoundException(String.format("ExchangeRate with id=%d not found", id));
            }
            return foundExchangeRate;
        } catch (SQLException | ClassNotFoundException e) {
            logger.debug("Error when get exchange by id");
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public List<ExchangeRate> getAll() throws InternalServerErrorException {
        String getAllQuery = "SELECT * FROM exchange_rates";
        List<ExchangeRate> foundExchangeRates = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Currency baseCurrency = currencyDao
                        .getCurrencyById(resultSet.getInt("base_currency_id"));
                Currency targetCurrency = currencyDao
                        .getCurrencyById(resultSet.getInt("target_currency_id"));

                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setId(resultSet.getInt("id"));
                exchangeRate.setBaseCurrency(baseCurrency);
                exchangeRate.setTargetCurrency(targetCurrency);
                exchangeRate.setRate(resultSet.getDouble("rate"));
                foundExchangeRates.add(exchangeRate);
            }
            logger.debug("All exchanges found");
        } catch (SQLException | ClassNotFoundException e) {
            logger.debug("Error when get all exchanges");
            throw new InternalServerErrorException(e);
        }

        return foundExchangeRates;
    }

    @Override
    public void deleteById(Integer id) throws InternalServerErrorException, NotFoundException {
        String deleteByIdQuery = "DELETE FROM exchange_rates WHERE id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteByIdQuery)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                logger.debug("ExchangeRate id={} deleted successfully", id);
            } else {
                logger.debug("Failed to delete exchangeRate with id={}", id);
                throw new NotFoundException(String.format("Failed to delete exchangeRate with id=%d", id));
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error when deleting currency");
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public void deleteByCurrencyId(Integer id) {
        String deleteExchangeRateByCurrencyIdQuery = "DELETE FROM exchange_rates WHERE base_currency_id = ? OR target_currency_id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedExchangeRateStatement = connection.prepareStatement(deleteExchangeRateByCurrencyIdQuery)) {

            preparedExchangeRateStatement.setInt(1, id);
            preparedExchangeRateStatement.setInt(2, id);
            preparedExchangeRateStatement.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error when deleting currency from exchangeRate");
        }
    }
}
