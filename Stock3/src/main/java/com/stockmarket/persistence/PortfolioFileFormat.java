package com.stockmarket.persistence;

public final class PortfolioFileFormat {
    private PortfolioFileFormat() {}

    public static final String SEP = "\\|"; // regex split
    public static final String OUT_SEP = "|";

    public static final String HEADER = "HEADER";
    public static final String CASH = "CASH";

    public static final String ASSET = "ASSET";
    public static final String LOT = "LOT";
}