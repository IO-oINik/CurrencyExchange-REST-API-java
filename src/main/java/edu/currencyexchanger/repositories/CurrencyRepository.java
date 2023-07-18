package edu.currencyexchanger.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import edu.currencyexchanger.model.Currency;
import java.util.List;
import java.util.Optional;

public class CurrencyRepository {

    private final Connection connection;

    public CurrencyRepository(Connection connection){
        this.connection = connection;
    }

    public Optional<Currency> findById(int id) {
        final String query = "SELECT * FROM Currencies WHERE ID="+id;
        Currency currency = null;
        try {
            Statement state = connection.createStatement();
            state.execute(query);
            ResultSet resultSet = state.getResultSet();
            if(resultSet.next()) {
                currency = createCurrency(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(currency);
    }

    private Currency createCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(resultSet.getInt("ID"), resultSet.getString("Code"),
                resultSet.getString("FullName"), resultSet.getString("Sign"));
    }

    public List<Currency> findAll() {
        final String query = "SELECT * FROM Currencies";
        List<Currency> list = new ArrayList<>();
        try {
            Statement state = connection.createStatement();
            state.execute(query);
            ResultSet resultSet = state.getResultSet();
            while(resultSet.next()) {
                list.add(createCurrency(resultSet));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Optional<Currency> findByCode(String code) {
        final String query = "SELECT * FROM Currencies WHERE Code=\"" + code + "\"";
        Currency currency = null;
        try {
            Statement state = connection.createStatement();
            state.execute(query);
            ResultSet resultSet = state.getResultSet();
            if(resultSet.next()) {
                currency = createCurrency(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(currency);
    }
    public boolean save(Currency currency) {
        final String query = "INSERT INTO Currencies (Code, FullName, Sign) VALUES ('"+currency.getCode() + "', \'" + currency.getFullName() + "', '" + currency.getSign() + "')";
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
