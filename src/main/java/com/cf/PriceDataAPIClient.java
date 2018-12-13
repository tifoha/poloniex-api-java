package com.cf;

/**
 *
 * @author David
 */
public interface PriceDataAPIClient
{
    public String returnTicker();

    public String getChartData(String currencyPair, Long periodInSeconds, Long startEpochSeconds);

    public String getChartData(String currencyPair, Long periodInSeconds, Long startEpochSeconds, Long endEpochSeconds);

    public String returnTradeHistory(String currencyPair, Long startEpochSeconds, Long endEpochSeconds);

    String getCurrencies();
}
