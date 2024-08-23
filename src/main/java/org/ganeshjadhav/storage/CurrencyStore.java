package org.ganeshjadhav.storage;

import org.ganeshjadhav.model.Stock;

public interface CurrencyStore extends BaseStore<String, Stock>{
    Stock getCurrencyBySymbol(String symbol);
}
