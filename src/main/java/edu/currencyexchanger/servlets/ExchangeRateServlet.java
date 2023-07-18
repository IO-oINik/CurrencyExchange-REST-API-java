package edu.currencyexchanger.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.currencyexchanger.model.Currency;
import edu.currencyexchanger.model.ExchangeRate;
import edu.currencyexchanger.repositories.CurrencyRepository;
import edu.currencyexchanger.repositories.ExchangeRatesRepository;
import edu.currencyexchanger.utils.Util;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Optional;

@MultipartConfig
@WebServlet(name = "exchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRatesRepository exchangeRatesRepository;
    private CurrencyRepository currencyRepository;

    @Override
    public void init(ServletConfig config) {
        exchangeRatesRepository = (ExchangeRatesRepository) config.getServletContext().getAttribute("exchangeRatesRepository");
        currencyRepository = (CurrencyRepository) config.getServletContext().getAttribute("currencyRepository");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (!method.equals("PATCH")) {
            super.service(req, resp);
        }
        else {
            this.doPatch(req, resp);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String coupleCode = request.getPathInfo().replaceFirst("/", "");
        if(coupleCode.length() != 6) {
            response.setStatus(400);
            Util.sendJsonMessage(response.getWriter(), "Коды валют пары отсутствуют в адресе");
            return;
        }
        String baseCurrencyCode = coupleCode.substring(0, 3);
        String targetCurrencyCode = coupleCode.substring(3, 6);
        Optional<Currency> baseCurrency = currencyRepository.findByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyRepository.findByCode(targetCurrencyCode);
        if(baseCurrency.isEmpty()) {
            response.setStatus(401);
            Util.sendJsonMessage(response.getWriter(), "Некрректные данные baseCurrencyCode");
            return;
        }
        if(targetCurrency.isEmpty()) {
            response.setStatus(402);
            Util.sendJsonMessage(response.getWriter(), "Некрректные данные targetCurrencyCode");
            return;
        }
        Optional<ExchangeRate> exchangeRate = exchangeRatesRepository.findByCurrencies(baseCurrency.get(), targetCurrency.get());
        if(exchangeRate.isEmpty()) {
            response.setStatus(404);
            Util.sendJsonMessage(response.getWriter(), "Обменный курс для пары не найден");
            return;
        }
        new ObjectMapper().writeValue(response.getWriter(), exchangeRate.get());
    }

    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String coupleCode = request.getPathInfo().replaceFirst("/", "");

        String rate = Util.getStringFromPartName(request, "rate");
        if(coupleCode.length() != 6) {
            response.setStatus(400);
            Util.sendJsonMessage(response.getWriter(), "Коды валют пары отсутствуют в адресе");
            return;
        }
        if(rate == null || !Util.isDouble(rate)) {
            response.setStatus(403);
            Util.sendJsonMessage(response.getWriter(), "Параметр rate не был передан или некорректный");
            return;
        }
        String baseCurrencyCode = coupleCode.substring(0, 3);
        String targetCurrencyCode = coupleCode.substring(3, 6);
        if(baseCurrencyCode.equals(targetCurrencyCode)) {
            response.setStatus(405);
            Util.sendJsonMessage(response.getWriter(), "baseCurrencyCode рваен targetCurrencyCode");
            return;
        }
        Optional<Currency> baseCurrency = currencyRepository.findByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyRepository.findByCode(targetCurrencyCode);
        if(baseCurrency.isEmpty()) {
            response.setStatus(401);
            Util.sendJsonMessage(response.getWriter(), "Некрректные данные baseCurrencyCode");
            return;
        }
        if(targetCurrency.isEmpty()) {
            response.setStatus(402);
            Util.sendJsonMessage(response.getWriter(), "Некрректные данные targetCurrencyCode");
            return;
        }
        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency.get(), targetCurrency.get(), Double.parseDouble(rate));
        if(!exchangeRatesRepository.update(exchangeRate)) {
            response.setStatus(404);
            Util.sendJsonMessage(response.getWriter(), "Валютная пара отсутствует в базе данных");
            return;
        }
        exchangeRate.setID(exchangeRatesRepository.findByCurrencies(baseCurrency.get(), targetCurrency.get()).get().getID());
        new ObjectMapper().writeValue(response.getWriter(), exchangeRate);
    }
}
