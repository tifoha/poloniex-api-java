package com.cf.data.model.poloniex;

import com.cf.util.JsonUtils;

import java.math.BigDecimal;

/**
 * @author Vitalii Sereda
 */
public class PoloniexCurrency {
    private int id;
    private String symbol;
    private String name;
    private BigDecimal txFee = BigDecimal.ZERO;
    private int minConf;
    private String depositAddress;
    private boolean disabled;
    private boolean delisted;
    private boolean frozen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getTxFee() {
        return txFee;
    }

    public void setTxFee(BigDecimal txFee) {
        this.txFee = txFee;
    }

    public int getMinConf() {
        return minConf;
    }

    public void setMinConf(int minConf) {
        this.minConf = minConf;
    }

    public String getDepositAddress() {
        return depositAddress;
    }

    public void setDepositAddress(String depositAddress) {
        this.depositAddress = depositAddress;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDelisted() {
        return delisted;
    }

    public void setDelisted(boolean delisted) {
        this.delisted = delisted;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }
}
