package org.ganeshjadhav.matchingEngine.store;

import org.ganeshjadhav.matchingEngine.model.BookOrder;
import org.ganeshjadhav.matchingEngine.store.OrderBook;

import java.util.*;

public class BuyerOrderBook implements OrderBook {
    private final TreeMap<Double, Queue<BookOrder>> buyOrders;

    public BuyerOrderBook() {
        buyOrders = new TreeMap<>((a,b) -> (int) (b - a) );
    }

    @Override
    public void addOrder(BookOrder order) {
        buyOrders.computeIfAbsent(order.getPrice(), k -> new LinkedList<>()).add(order);
    }

    @Override
    public void removeOrder(BookOrder order) {
        Queue<BookOrder> orders = buyOrders.get(order.getPrice());
        if(orders != null){
            orders.remove(order);
        }
    }

    @Override
    public BookOrder getBestOrder(){
        return buyOrders.isEmpty() ? null : buyOrders.firstEntry().getValue().peek();
    }

    public TreeMap<Double, Queue<BookOrder>> getBuyOrders(){
        return buyOrders;
    }
}
