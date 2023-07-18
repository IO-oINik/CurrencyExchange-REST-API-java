package edu.currencyexchanger.listners;

import edu.currencyexchanger.repositories.CurrencyRepository;
import edu.currencyexchanger.repositories.ExchangeRatesRepository;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebListener
public class AppContextListner implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:E:/java_projects/CurrencyExchanger/src/main/resources/db.sqlite");
            CurrencyRepository currencyRepository = new CurrencyRepository(connection);
            ExchangeRatesRepository exchangeRatesRepository = new ExchangeRatesRepository(connection, currencyRepository);
            context.setAttribute("currencyRepository", currencyRepository);
            context.setAttribute("exchangeRatesRepository", exchangeRatesRepository);
            System.out.println("Database connection initialized for Application.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        Connection connection = (Connection) context.getAttribute("connection");
        try {
            connection.close();
            System.out.println("Database connection closed for Application.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
