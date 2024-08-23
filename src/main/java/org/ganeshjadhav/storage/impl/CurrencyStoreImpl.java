package org.ganeshjadhav.storage.impl;
import org.ganeshjadhav.model.Stock;
import org.ganeshjadhav.storage.CurrencyStore;

import java.util.HashMap;
import java.util.Map;

public class CurrencyStoreImpl implements CurrencyStore {

    private final Map<String, Stock> currencyMap;
    private final Map<String, Stock> symbolCurrencyMap;

    public CurrencyStoreImpl() {
        this.currencyMap = new HashMap<>();
        this.symbolCurrencyMap = new HashMap<>();
    }
    @Override
    public Stock put(Stock stock) {
        currencyMap.put(stock.getSymbol(), stock);
        symbolCurrencyMap.put(stock.getSymbol(), stock);
        return stock;
    }

    @Override
    public Stock get(String symbol) {
        return currencyMap.get(symbol);
    }

    @Override
    public void delete(String symbol) {
        currencyMap.remove(symbol);
    }

    @Override
    public Stock getCurrencyBySymbol(String symbol) {
        return symbolCurrencyMap.get(symbol);
    }
}
