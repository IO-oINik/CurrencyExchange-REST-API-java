package edu.currencyexchanger.repositories;

import edu.currencyexchanger.model.Currency;
import edu.currencyexchanger.model.ExchangeRate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesRepository {
    private final Connection connection;
    private final CurrencyRepository currencyRepository;

    public ExchangeRatesRepository(Connection connection, CurrencyRepository currencyRepository) {
        this.connection = connection;
        this.currencyRepository = currencyRepository;
    }

    private ExchangeRate createExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(resultSet.getInt("ID"),
                currencyRepository.findById(resultSet.getInt("BaseCurrencyId")).get(),
                currencyRepository.findById(resultSet.getInt("TargetCurrencyId")).get(),
                resultSet.getDouble("Rate"));
    }

    public List<ExchangeRate> findAll() {
        final String query = "SELECT * FROM ExchangeRates";
        List<ExchangeRate> list = new ArrayList<>();
        try {
            Statement state = connection.createStatement();
            state.execute(query);
            ResultSet resultSet = state.getResultSet();
            while (resultSet.next()) {
                list.add(createExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Optional<ExchangeRate> findByCurrencies(Currency baseCurrency, Currency targetCurrency) {
        final String query = "SELECT * FROM ExchangeRates WHERE BaseCurrencyID=" + baseCurrency.getID() + " AND TargetCurrencyId=" + targetCurrency.getID();
        ExchangeRate exchangeRate = null;
        try {
            Statement state = connection.createStatement();
            state.execute(query);
            ResultSet resultSet = state.getResultSet();
            if(resultSet.next()) {
                exchangeRate = createExchangeRate(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(exchangeRate);
    }

    public boolean save(ExchangeRate exchangeRate) {
        final String query = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES(" + exchangeRate.getBaseCurrency().getID() + ", " + exchangeRate.getTargetCurrency().getID() + ", " + exchangeRate.getRate() + ")";
        try {
            Statement state = connection.createStatement();
            state.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    public boolean update(ExchangeRate exchangeRate) {
        final String query = "UPDATE ExchangeRates SET Rate=" + exchangeRate.getRate() + " WHERE BaseCurrencyId=" + exchangeRate.getBaseCurrency().getID() + " AND TargetCurrencyId=" + exchangeRate.getTargetCurrency().getID();
        try {
            Statement state = connection.createStatement();
            state.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
