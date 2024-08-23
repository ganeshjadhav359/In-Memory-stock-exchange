package org.ganeshjadhav.service;

import org.ganeshjadhav.exception.NotFoundException;
import org.ganeshjadhav.matchingEngine.MatchingEngine;
import org.ganeshjadhav.model.Stock;
import org.ganeshjadhav.storage.CurrencyStore;

public class CurrencyService {
    private final CurrencyStore currencyStore;
    private final MatchingEngine matchingEngine;

    public CurrencyService(CurrencyStore currencyStore, MatchingEngine matchingEngine) {
        this.currencyStore = currencyStore;
        this.matchingEngine = matchingEngine;
    }



    public Stock create(String name, String symbol, double minQty, double maxQty) throws Exception {
        Stock stock = Stock.build(name, symbol, minQty, maxQty);
        if(currencyStore.getCurrencyBySymbol(stock.getSymbol()) != null){
            throw new Exception("Currency already exist with given symbol");
        }
        currencyStore.put(stock);
        matchingEngine.createOrderBook(stock.getSymbol());

        return stock;
    }

    public Stock get(String currencyId){
        if(currencyStore.get(currencyId) == null){
            throw new NotFoundException("Currency not found");
        }
        return currencyStore.get(currencyId);
    }
}
