package edu.currencyexchanger.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.currencyexchanger.model.Currency;
import edu.currencyexchanger.repositories.CurrencyRepository;
import edu.currencyexchanger.utils.Util;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "currencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyRepository currencyRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyRepository = (CurrencyRepository) config.getServletContext().getAttribute("currencyRepository");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String CodeOrId = request.getPathInfo();
        if(CodeOrId == null || CodeOrId.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\n \"message\": \"Указана не корректная валюта. Пример: .../currency/USD\" \n}");
            return;
        }
        CodeOrId = CodeOrId.replaceFirst("/", "");
        Optional<Currency> currency = Optional.empty();
        String messageError;
        if(Util.isInteger(CodeOrId)) {
            currency = currencyRepository.findById(Integer.parseInt(CodeOrId));
            messageError = "{\n \"message\": \"Валюты с таким ID не существует.\" \n}";

        } else {
            currency = currencyRepository.findByCode(CodeOrId);
            messageError = "{\n \"message\": \"Валюты с таким кодом не существует.\" \n}";
        }
        if(currency.isPresent()) {
            new ObjectMapper().writeValue(response.getWriter(), currency.get());
        } else {
            response.setStatus(404);
            response.getWriter().print(messageError);
        }
    }
}
