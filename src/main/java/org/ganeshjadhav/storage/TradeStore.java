package org.ganeshjadhav.storage;

import lombok.Value;
import org.ganeshjadhav.model.Trade;

import java.security.Key;
import java.util.List;

public interface TradeStore extends BaseStore<String, Trade> {
    List<Trade> buyerTrades(Trade trade);
    List<Trade>  sellerTrades(Trade trade);
}
