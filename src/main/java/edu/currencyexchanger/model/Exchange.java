package edu.currencyexchanger.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Exchange {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;
    private double amount;
    private double convertedAmount;
}
