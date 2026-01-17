package com.stockmarket.persistence;

/**
 Stałe formatu pliku persystencji.
 Separator:
 - zapis: '|'
 - odczyt: split po regexie "\\|"
 */
public final class PortfolioFileFormat {
    private PortfolioFileFormat() {}

    public static final String SEP = "\\|";      // regex do split
    public static final String OUT_SEP = "|";    // separator w zapisie

    // tagi linii w pliku
    public static final String HEADER = "HEADER";
    public static final String CASH = "CASH";
    public static final String ASSET = "ASSET";
    public static final String LOT = "LOT";
}