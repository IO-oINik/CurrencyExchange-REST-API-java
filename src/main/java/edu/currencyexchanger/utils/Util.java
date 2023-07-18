package edu.currencyexchanger.utils;

import edu.currencyexchanger.model.Currency;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.stream.Collectors;

public class Util {
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isValidCurrency(Currency currency) {
         if(currency == null || currency.getClass() != Currency.class) {
             return false;
         }
         return currency.getCode() != null && currency.getSign() != null && currency.getFullName() != null;
    }
    public static void sendJsonMessage(PrintWriter writer, String message) {
        writer.println("{\n \"message\": \""+ message + "\" \n}");
    }

    public static String getStringFromPartName(HttpServletRequest request, String partName) {
        try {
            Part part = request.getPart(partName);
            return new BufferedReader(new InputStreamReader(part.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
        } catch (ServletException | IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
