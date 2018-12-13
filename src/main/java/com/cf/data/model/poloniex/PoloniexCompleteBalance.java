package com.cf.data.model.poloniex;

import com.cf.util.JsonUtils;

import java.math.BigDecimal;

/**
 *
 * @author David
 */
public class PoloniexCompleteBalance
{
    public final BigDecimal available;
    public final BigDecimal onOrders;
    public final BigDecimal btcValue;

    public PoloniexCompleteBalance(BigDecimal available, BigDecimal onOrders, BigDecimal btcValue)
    {
        this.available = available;
        this.onOrders = onOrders;
        this.btcValue = btcValue;
    }

    @Override
    public String toString()
    {
        return JsonUtils.GSON.toJson(this);
    }
}
