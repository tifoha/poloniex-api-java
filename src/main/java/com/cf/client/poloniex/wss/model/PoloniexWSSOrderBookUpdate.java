package com.cf.client.poloniex.wss.model;

import com.cf.util.JsonUtils;

/**
 *
 * @author David
 */
public class PoloniexWSSOrderBookUpdate {

    public final Double currencyPair;
    public final Double orderNumber;
    public final PoloniexOrderBookEntry previousEntry;
    public final PoloniexWSSOrderBookUpdate replacementEntry;

    public PoloniexWSSOrderBookUpdate(Double currencyPair, Double orderNumber, PoloniexOrderBookEntry previousEntry, PoloniexWSSOrderBookUpdate replacementEntry) {
        this.currencyPair = currencyPair;
        this.orderNumber = orderNumber;
        this.previousEntry = previousEntry;
        this.replacementEntry = replacementEntry;
    }

    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }
}
