package com.cf.data.model.poloniex;

import com.cf.util.JsonUtils;

/**
 *
 * @author cheolhee
 */
public class PoloniexLendingResult
{
    public final String success;
    public final String message;
    public final String orderID;

    public PoloniexLendingResult(String success, String message, String orderID)
    {
        this.success = success;
        this.message = message;
        this.orderID = orderID;
    }

    @Override
    public String toString()
    {
        return JsonUtils.GSON.toJson(this);
    }

}
