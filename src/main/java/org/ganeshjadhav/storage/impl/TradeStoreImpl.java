package org.ganeshjadhav.storage.impl;

import org.ganeshjadhav.model.Trade;
import org.ganeshjadhav.storage.BaseStore;
import org.ganeshjadhav.storage.TradeStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeStoreImpl implements TradeStore {

    private final Map<String, Trade> tradeMap;
    private final Map<String, List<Trade>> buyerOrderTreads;
    private final Map<String, List<Trade>> sellerOrderTreads;

    public TradeStoreImpl() {
        tradeMap = new HashMap<>();
        buyerOrderTreads = new HashMap<>();
        sellerOrderTreads = new HashMap<>();
    }

    @Override
    public Trade put(Trade trade) {
        tradeMap.put(trade.getId(), trade);
        return trade;
    }

    @Override
    public Trade get(String  id) {
        return tradeMap.get(id);
    }

    @Override
    public void delete(String id) {
        tradeMap.remove(id);
    }

    @Override
    public List<Trade> buyerTrades(Trade trade){
        List<Trade> trades = buyerOrderTreads.getOrDefault(trade.getBuyerOrderId(), new ArrayList<>());
        trades.add(trade);
        buyerOrderTreads.put(trade.getBuyerOrderId(), trades);
        return null;
    }

    @Override
    public List<Trade> sellerTrades(Trade trade){
        List<Trade> trades = sellerOrderTreads.getOrDefault(trade.getSellerOrderId(), new ArrayList<>());
        trades.add(trade);
        sellerOrderTreads.put(trade.getSellerOrderId(), trades);
        return  null;
    }
}
