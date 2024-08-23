package org.ganeshjadhav.matchingEngine;

import org.ganeshjadhav.matchingEngine.model.BookOrder;
import org.ganeshjadhav.matchingEngine.store.OrderBook;
import org.ganeshjadhav.model.Trade;

import java.util.List;

public interface MatchingStrategy {
    public void match(BookOrder order, OrderBook oppositeOrderBok, List<Trade> trades);
}
