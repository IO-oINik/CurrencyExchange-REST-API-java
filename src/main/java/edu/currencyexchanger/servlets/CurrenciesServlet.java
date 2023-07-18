package edu.currencyexchanger.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.currencyexchanger.repositories.CurrencyRepository;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import edu.currencyexchanger.model.Currency;
import edu.currencyexchanger.utils.Util;

import java.io.IOException;

@WebServlet(name = "currenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyRepository currencyRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyRepository = (CurrencyRepository) config.getServletContext().getAttribute("currencyRepository");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), currencyRepository.findAll());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        Currency newCurrency = new Currency(request.getParameter("Code"), request.getParameter("FullName"), request.getParameter("Sign"));
        if(!Util.isValidCurrency(newCurrency)){
            response.setStatus(400);
            Util.sendJsonMessage(response.getWriter(),"Отсутствует нужное поле формы");
            return;
        }
        if(!currencyRepository.save(newCurrency)) {
            response.setStatus(500);
            Util.sendJsonMessage(response.getWriter(),"Ошибка сервера");
            return;
        }
        new ObjectMapper().writeValue(response.getWriter(), newCurrency);
    }
}
