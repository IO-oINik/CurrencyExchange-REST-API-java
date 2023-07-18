package edu.currencyexchanger.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Currency {

    private int ID;
    @NonNull
    private String Code;
    @NonNull
    private String FullName;
    @NonNull
    private String Sign;
}
