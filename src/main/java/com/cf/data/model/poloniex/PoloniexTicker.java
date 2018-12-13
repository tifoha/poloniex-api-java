package com.cf.data.model.poloniex;

import com.cf.util.JsonUtils;

import java.math.BigDecimal;

/**
 * @author David
 */
public class PoloniexTicker {
    public final Integer id;
    public final BigDecimal last;
    public final BigDecimal lowestAsk;
    public final BigDecimal highestBid;
    public final BigDecimal percentChange;
    public final BigDecimal baseVolume;
    public final BigDecimal quoteVolume;

    public PoloniexTicker(Integer id, BigDecimal last, BigDecimal lowestAsk, BigDecimal highestBid, BigDecimal percentChange, BigDecimal baseVolume, BigDecimal quoteVolume) {
        this.id = id;
        this.last = last;
        this.lowestAsk = lowestAsk;
        this.highestBid = highestBid;
        this.percentChange = percentChange;
        this.baseVolume = baseVolume;
        this.quoteVolume = quoteVolume;
    }

    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }
}
