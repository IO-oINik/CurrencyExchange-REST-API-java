package edu.currencyexchanger.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.currencyexchanger.model.Currency;
import edu.currencyexchanger.model.ExchangeRate;
import edu.currencyexchanger.repositories.CurrencyRepository;
import edu.currencyexchanger.repositories.ExchangeRatesRepository;
import edu.currencyexchanger.utils.Util;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "ExchangeRatesServlet", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRatesRepository exchangeRatesRepository;
    private CurrencyRepository currencyRepository;

    @Override
    public void init(ServletConfig config) {
        exchangeRatesRepository = (ExchangeRatesRepository) config.getServletContext().getAttribute("exchangeRatesRepository");
        currencyRepository = (CurrencyRepository) config.getServletContext().getAttribute("currencyRepository");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), exchangeRatesRepository.findAll());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rate = request.getParameter("rate");
        if(baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
            response.setStatus(400);
            Util.sendJsonMessage(response.getWriter(), "Отсутствует нужное поле формы");
            return;
        }
        if(!Util.isDouble(rate)) {
            response.setStatus(401);
            Util.sendJsonMessage(response.getWriter(), "Некорреткное значение поля rate");
            return;
        }
        Optional<Currency> baseCurrency = currencyRepository.findByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyRepository.findByCode(targetCurrencyCode);
        if(baseCurrency.isEmpty()) {
            response.setStatus(402);
            Util.sendJsonMessage(response.getWriter(), "Некорректное значение поля baseCurrencyCode");
            return;
        }
        if(targetCurrency.isEmpty()) {
            response.setStatus(403);
            Util.sendJsonMessage(response.getWriter(), "Некорректное значение поля targetCurrencyCode");
            return;
        }
        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency.get(), targetCurrency.get(), Double.parseDouble(rate));
        if(!exchangeRatesRepository.save(exchangeRate)) {
            response.setStatus(409);
            Util.sendJsonMessage(response.getWriter(), "Валютная пара с таким кодом уже существует");
            return;
        }
        exchangeRate.setID(exchangeRatesRepository.findByCurrencies(baseCurrency.get(), targetCurrency.get()).get().getID());
        new ObjectMapper().writeValue(response.getWriter(), exchangeRate);
    }
}
