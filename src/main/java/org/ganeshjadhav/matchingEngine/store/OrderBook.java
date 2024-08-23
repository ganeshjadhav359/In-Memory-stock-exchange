package org.ganeshjadhav.matchingEngine.store;

import org.ganeshjadhav.matchingEngine.model.BookOrder;

public interface OrderBook {
    void addOrder(BookOrder order);
    void removeOrder(BookOrder order);
    BookOrder getBestOrder();
}
