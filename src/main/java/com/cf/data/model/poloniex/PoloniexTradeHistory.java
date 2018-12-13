package com.cf.data.model.poloniex;

import com.cf.util.JsonUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 *
 * @author David
 */
public class PoloniexTradeHistory
{
    public final Long globalTradeID;
    public final String tradeID;
    public final ZonedDateTime date;
    public final BigDecimal rate;
    public final BigDecimal amount;
    public final BigDecimal total;
    public final BigDecimal fee;
    public final String orderNumber;
    public final String type;
    public final String category;

    public PoloniexTradeHistory(Long globalTradeID, String tradeID, ZonedDateTime date, BigDecimal rate, BigDecimal amount, BigDecimal total, BigDecimal fee, String orderNumber, String type, String category)
    {
        this.globalTradeID = globalTradeID;
        this.tradeID = tradeID;
        this.date = date;
        this.rate = rate;
        this.amount = amount;
        this.total = total;
        this.fee = fee;
        this.orderNumber = orderNumber;
        this.type = type;
        this.category = category;
    }

    public Long getGlobalTradeID() {
        return globalTradeID;
    }

    public String getTradeID() {
        return tradeID;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString()
    {
        return JsonUtils.GSON.toJson(this);
    }
}
