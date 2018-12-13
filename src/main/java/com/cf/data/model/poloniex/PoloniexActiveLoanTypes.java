package com.cf.data.model.poloniex;

import com.cf.util.JsonUtils;

import java.util.List;

/**
 *
 * @author David
 */
public class PoloniexActiveLoanTypes
{
    public final List<PoloniexLoan> used;
    public final List<PoloniexLoan> provided;

    public PoloniexActiveLoanTypes(List<PoloniexLoan> used, List<PoloniexLoan> provided)
    {        
        this.used = used;
        this.provided = provided;
    }

    @Override
    public String toString()
    {
        return JsonUtils.GSON.toJson(this);
    }
}
