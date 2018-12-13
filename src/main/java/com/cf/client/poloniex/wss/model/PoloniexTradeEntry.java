package com.cf.client.poloniex.wss.model;

import com.cf.util.JsonUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author Vitalii Sereda
 */
public class PoloniexTradeEntry {
    public final String tradeID;
    public final String type;
    public final BigDecimal amount;
    public final BigDecimal rate;
    public final ZonedDateTime date;

    public PoloniexTradeEntry(String tradeID, String type, BigDecimal rate, BigDecimal amount, ZonedDateTime date) {
        this.tradeID = tradeID;
        this.type = type;
        this.rate = rate;
        this.amount = amount;
        this.date = date;
    }

    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }
}
