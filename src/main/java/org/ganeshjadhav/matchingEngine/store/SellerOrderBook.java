package org.ganeshjadhav.matchingEngine.store;

import org.ganeshjadhav.matchingEngine.model.BookOrder;
import org.ganeshjadhav.matchingEngine.store.OrderBook;

import java.util.*;

public class SellerOrderBook implements OrderBook {
    private final TreeMap<Double, Queue<BookOrder>> sellerOrders;

    public SellerOrderBook() {
        sellerOrders = new TreeMap<>();
    }

    @Override
    public void addOrder(BookOrder order) {
        sellerOrders.computeIfAbsent(order.getPrice(), k -> new LinkedList<>()).add(order);
    }

    @Override
    public void removeOrder(BookOrder order) {
        Queue<BookOrder> orders = sellerOrders.get(order.getPrice());
        if(orders != null){
            orders.remove(order);
        }
    }
    public BookOrder getBestOrder(){
        return sellerOrders.isEmpty() ? null : sellerOrders.firstEntry().getValue().peek();
    }

    public TreeMap<Double, Queue<BookOrder>> getSellOrders(){
        return sellerOrders;
    }

}
