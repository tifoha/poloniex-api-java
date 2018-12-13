package com.cf.client.poloniex;

import com.cf.PriceDataAPIClient;
import com.cf.client.HTTPClient;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

/**
 * @author David
 */
public class PoloniexPublicAPIClient implements PriceDataAPIClient {
    private static final String PUBLIC_URL = "https://poloniex.com/public?";
    private static final int DAY = 60 * 60 * 24;

    private final HTTPClient client;

    public PoloniexPublicAPIClient(HTTPClient client) {
        this.client = client;
    }

    @Override
    public String returnTicker() {
        try {
            String url = PUBLIC_URL + "command=returnTicker";
            return client.getHttp(url, null);
        } catch (IOException ex) {
            LogManager.getLogger(PoloniexPublicAPIClient.class).warn("Call to return ticker API resulted in exception - " + ex.getMessage(), ex);
        }

        return null;
    }

    @Override
    public String getChartData(String currencyPair, Long periodInSeconds, Long startEpochSeconds) {
        return getChartData(currencyPair, periodInSeconds, startEpochSeconds, 9999999999L);
    }

    @Override
    public String getChartData(String currencyPair, Long periodInSeconds, Long startEpochSeconds, Long endEpochSeconds) {
        return getChartData(currencyPair, startEpochSeconds.toString(), endEpochSeconds.toString(), periodInSeconds.toString());
    }

    @Override
    public String returnTradeHistory(String currencyPair, Long startEpochSeconds, Long endEpochSeconds) {
        if ((endEpochSeconds - startEpochSeconds) / DAY > 31) {
            throw new RuntimeException("Period should be < 31 days");
        }

        try {
            String url = PUBLIC_URL + "command=returnTradeHistory&currencyPair=" + currencyPair + "&start=" + startEpochSeconds + "&end=" + endEpochSeconds;
            return client.getHttp(url, null);
        } catch (IOException ex) {
            LogManager.getLogger(PoloniexPublicAPIClient.class).warn("Call to Chart Data API resulted in exception - " + ex.getMessage(), ex);
        }

        return null;
    }

    @Override
    public String getCurrencies() {
        try {
            String url = PUBLIC_URL + "command=returnCurrencies";
            return client.getHttp(url, null);
        } catch (IOException ex) {
            LogManager.getLogger(PoloniexPublicAPIClient.class).warn("Call to Chart Data API resulted in exception - " + ex.getMessage(), ex);
        }

        return null;
    }

    private String getChartData(String currencyPair, String startEpochInSec, String endEpochInSec, String periodInSec) {
        try {
            String url = PUBLIC_URL + "command=returnChartData&currencyPair=" + currencyPair + "&start=" + startEpochInSec + "&end=" + endEpochInSec + "&period=" + periodInSec;
            return client.getHttp(url, null);
        } catch (IOException ex) {
            LogManager.getLogger(PoloniexPublicAPIClient.class).warn("Call to Chart Data API resulted in exception - " + ex.getMessage(), ex);
        }

        return null;
    }

}
