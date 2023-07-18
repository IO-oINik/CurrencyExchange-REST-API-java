package edu.currencyexchanger.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.currencyexchanger.model.Currency;
import edu.currencyexchanger.model.Exchange;
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
import java.util.Enumeration;
import java.util.Optional;

@WebServlet(name = "exchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {
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
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amount = request.getParameter("amount");

        if(from == null || to == null || amount == null) {
            response.setStatus(400);
            Util.sendJsonMessage(response.getWriter(), "Некорректные данные");
            return;
        }
        if(!Util.isDouble(amount)) {
            response.setStatus(405);
            Util.sendJsonMessage(response.getWriter(), "Параметр amount не корректный");
            return;
        }

        Optional<Currency> baseCurrency = currencyRepository.findByCode(from);
        Optional<Currency> targetCurrency = currencyRepository.findByCode(to);

        if(baseCurrency.isEmpty()) {
            response.setStatus(401);
            Util.sendJsonMessage(response.getWriter(), "Валюта to не найдена");
            return;
        }
        if(targetCurrency.isEmpty()) {
            response.setStatus(402);
            Util.sendJsonMessage(response.getWriter(), "Валюта from не найдена");
            return;
        }

        Optional<ExchangeRate> exchangeRate = exchangeRatesRepository.findByCurrencies(baseCurrency.get(), targetCurrency.get());
        Optional<ExchangeRate> reverseExchangeRate = Optional.empty();
        if(exchangeRate.isEmpty()) {
            reverseExchangeRate = exchangeRatesRepository.findByCurrencies(targetCurrency.get(), baseCurrency.get());
            if(reverseExchangeRate.isEmpty()) {
                response.setStatus(404);
                Util.sendJsonMessage(response.getWriter(), "Валюта не найдена");
                return;
            }
        }
        double rate;
        if (exchangeRate.isPresent()) {
            rate = exchangeRate.get().getRate();
        } else {
            rate = 1 / reverseExchangeRate.get().getRate();
        }
        Exchange exchange = new Exchange(baseCurrency.get(), targetCurrency.get(), rate, Double.parseDouble(amount), Double.parseDouble(amount)*rate);
        new ObjectMapper().writeValue(response.getWriter(), exchange);
    }
}
