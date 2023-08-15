package org.example.dao;

import org.example.DatabaseConnector;
import org.example.dto.CurrencyDto;
import org.example.model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDaoImpl implements CurrencyDao {
    private static final CurrencyDaoImpl INSTANCE = new CurrencyDaoImpl();
    private static final Logger logger = LoggerFactory.getLogger(CurrencyDaoImpl.class);

    private CurrencyDaoImpl() {
    }

    public static CurrencyDaoImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void create(CurrencyDto currencyDto) {
        String insertQuery = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, currencyDto.getCode());
            preparedStatement.setString(2, currencyDto.getName());
            preparedStatement.setString(3, currencyDto.getSign());

            int rowsAffected = preparedStatement.executeUpdate();
            logger.debug(rowsAffected > 0 ? "Currency saved successfully" : "Failed to save currency");

        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error when creating currency");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateById(Integer id, CurrencyDto currencyDto) {
        String updateQuery = "UPDATE currencies SET code = ?, full_name = ?, sign = ? WHERE id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, currencyDto.getCode());
            preparedStatement.setString(2, currencyDto.getName());
            preparedStatement.setString(3, currencyDto.getSign());
            preparedStatement.setInt(4, id);

            int rowsAffected = preparedStatement.executeUpdate();
            logger.debug(rowsAffected > 0 ? "Currency updated successfully" : "Failed to update currency");

        } catch (SQLException e) {
            logger.error("Error when updating currency");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Currency getCurrencyByCode(String code) {
        String getByCodeQuery = "SELECT * FROM currencies WHERE code = ?";
        Currency foundCurrency;

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getByCodeQuery)) {
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                foundCurrency = new Currency(
                        resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        code,
                        resultSet.getString("sign"));
                logger.debug("Currency with code={} successfully found", code);
            } else {
                logger.debug("Currency with code={} not found", code);
                throw new RuntimeException(String.format("Currency with code=%s not found", code));
            }
            return foundCurrency;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error when searching for a currency by code");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Currency getCurrencyById(Integer id) {
        String getByCodeQuery = "SELECT * FROM currencies WHERE id = ?";
        Currency foundCurrency;

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getByCodeQuery)) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                foundCurrency = new Currency(
                        id,
                        resultSet.getString("full_name"),
                        resultSet.getString("code"),
                        resultSet.getString("sign"));
                logger.debug("Currency with id={} successfully found", id);
            } else {
                logger.debug("Currency with id={} not found", id);
                throw new RuntimeException(String.format("Currency with id=%d not found", id));
            }
            return foundCurrency;
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error when searching for a currency by id");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Currency> getAll() {
        String getByCodeQuery = "SELECT * FROM currencies";
        List<Currency> foundCurrencies = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getByCodeQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Currency currency = new Currency(resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("code"),
                        resultSet.getString("sign"));
                foundCurrencies.add(currency);
            }
            logger.debug("All currencies found");
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error when searching for a currency. Error message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return foundCurrencies;
    }

    @Override
    public void deleteById(Integer id) throws RuntimeException {
        String deleteExchangeRateByCurrencyIdQuery = "DELETE FROM exchange_rates WHERE base_currency_id = ? OR target_currency_id = ?";
        String deleteCurrencyByIdQuery = "DELETE FROM currencies WHERE id = ? ";

        try (Connection connection = DatabaseConnector.getConnection()) {
            try (PreparedStatement preparedExchangeRateStatement = connection.prepareStatement(deleteExchangeRateByCurrencyIdQuery)) {
                preparedExchangeRateStatement.setInt(1, id);
                preparedExchangeRateStatement.setInt(2, id);
                preparedExchangeRateStatement.executeQuery();

            } catch (SQLException e) {
                logger.error("Error when deleting currency from exchangeRate");
            }

            try (PreparedStatement preparedCurrencyStatement = connection.prepareStatement(deleteCurrencyByIdQuery)) {
                preparedCurrencyStatement.setInt(1, id);
                int rowsAffected = preparedCurrencyStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.debug("Currency with id={} deleted successfully", id);
                } else {
                    logger.debug("Failed to delete currency with id={}", id);
                    throw new RuntimeException(String.format("Failed to delete currency with id=%d", id));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error when deleting currency");
            throw new RuntimeException(e);
        }
    }
}
