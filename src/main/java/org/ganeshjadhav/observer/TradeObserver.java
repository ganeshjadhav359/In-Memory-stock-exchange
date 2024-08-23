package org.ganeshjadhav.observer;

import org.ganeshjadhav.executor.TradeExecutor;
import org.ganeshjadhav.model.Trade;
import org.ganeshjadhav.service.OrderService;
import org.ganeshjadhav.storage.TradeStore;

public class TradeObserver {

    public TradeObserver() {
    }

    public void update(Trade trade){
        // we can use this to send to market data services;
        //tradeExecutor.executeTrade(trade);
    }
}
