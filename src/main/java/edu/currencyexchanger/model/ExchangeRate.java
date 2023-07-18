package edu.currencyexchanger.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ExchangeRate {
    private int ID;
    @NonNull
    private Currency BaseCurrency;
    @NonNull
    private Currency TargetCurrency;
    @NonNull
    private double Rate;
}
