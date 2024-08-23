package org.ganeshjadhav.matchingEngine;

import org.ganeshjadhav.exception.OrderBookNotFound;
import org.ganeshjadhav.exception.OrderCanNotBeModifiedNowException;
import org.ganeshjadhav.exception.OrderNotFound;
import org.ganeshjadhav.matchingEngine.model.BookOrder;
import org.ganeshjadhav.matchingEngine.store.BuyerOrderBook;
import org.ganeshjadhav.matchingEngine.store.OrderBook;
import org.ganeshjadhav.matchingEngine.store.SellerOrderBook;
import org.ganeshjadhav.model.OrderType;
import org.ganeshjadhav.model.Trade;
import org.ganeshjadhav.observer.CancelledOrderObserver;
import org.ganeshjadhav.observer.TradeObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MatchingEngine {
    private final ConcurrentHashMap<String, OrderBook> buyerOrderbook;
    private final ConcurrentHashMap<String, OrderBook> sellerOrderbook;
    private final ConcurrentHashMap<String, BookOrder> orderMap;
    private final List<TradeObserver> observers;
    private final MatchingStrategy matchingStrategy;
    private final Map<String, Object> lockObjects;
    private ScheduledExecutorService scheduler;

    private final List<CancelledOrderObserver> cancelledOrderObservers;

    public MatchingEngine() {
        buyerOrderbook = new ConcurrentHashMap<>();
        sellerOrderbook = new ConcurrentHashMap<>();
        orderMap = new ConcurrentHashMap<>();
        observers = new ArrayList<>();
        matchingStrategy = new PriceTimeMatchingStrategy();
        lockObjects = new TreeMap<>();
        cancelledOrderObservers = new ArrayList<>();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::removeExpiredOrders, 0, 1, TimeUnit.SECONDS);
    }

    public void createOrderBook(String symbol){
        buyerOrderbook.putIfAbsent(symbol, new BuyerOrderBook());
        sellerOrderbook.putIfAbsent(symbol, new SellerOrderBook());
        lockObjects.putIfAbsent(symbol, new Object());
    }

    public void addObserver(TradeObserver observer){
        observers.add(observer);
    }
    public void addCancelledOrderObserver(CancelledOrderObserver observer){
        cancelledOrderObservers.add(observer);
    }

    public List<Trade> addOrder(String orderId, String symbol, OrderType orderType, double qty, double price, long timestamp, long ttl){
        if(buyerOrderbook.get(symbol) == null && sellerOrderbook.get(symbol) == null){
            throw new OrderBookNotFound("order book not available for given symbol");
        }

        synchronized (lockObjects.get(symbol)){
            BookOrder order = new BookOrder(orderId, symbol, orderType, qty, price, timestamp, ttl);

            OrderBook orderBook = (order.getOrdeType() == OrderType.BUY)
                    ? buyerOrderbook.computeIfAbsent(order.getSymbol(), k -> new BuyerOrderBook())
                    : sellerOrderbook.computeIfAbsent(order.getSymbol(), k -> new SellerOrderBook());

            OrderBook oppositeOrderBook = (order.getOrdeType() == OrderType.BUY)
                    ? sellerOrderbook.computeIfAbsent(order.getSymbol(), k -> new SellerOrderBook())
                    : buyerOrderbook.computeIfAbsent(order.getSymbol(), k -> new BuyerOrderBook());
            List<Trade> trades = new ArrayList<>();
            matchingStrategy.match(order, oppositeOrderBook, trades);
            if(order.getQty() > 0){
                orderBook.addOrder(order);
                orderMap.put(order.getId(),order);
            }
            return trades;
            //notifyTrades(trades);
        }

    }

    public List<Trade> updateOrder(String orderId, double originalQty, double newQty, double price){
        BookOrder order = orderMap.get(orderId);
        if(order == null)
            throw new OrderNotFound("Order not found exception");
        synchronized (order.getSymbol()){
            order = orderMap.get(orderId);
            if(order == null || order.getQty() != originalQty){
                throw new OrderCanNotBeModifiedNowException("order can not be modified now.");
            }
            OrderBook orderBook = order.getOrdeType() == OrderType.BUY
                    ? buyerOrderbook.get(order.getSymbol()) : sellerOrderbook.get(order.getSymbol());
            orderBook.removeOrder(order);
            order.setQty(newQty);
            order.setPrice(price);

            OrderBook oppositeOrderBook = (order.getOrdeType() == OrderType.BUY)
                    ? sellerOrderbook.computeIfAbsent(order.getSymbol(), k -> new SellerOrderBook())
                    : buyerOrderbook.computeIfAbsent(order.getSymbol(), k -> new BuyerOrderBook());

            List<Trade> trades = new ArrayList<>();
            matchingStrategy.match(order, oppositeOrderBook, trades);

            if(order.getQty() > 0){
                orderBook.addOrder(order);
                orderMap.put(order.getId(),order);
            }else{
                orderMap.remove(order.getId());
            }
            return trades;
//            notifyTrades(trades);
        }
    }

    public void cancelOrder(String orderId){
        BookOrder order = orderMap.get(orderId);
        if(order == null)
            throw new OrderNotFound("Order not found exception");
        synchronized (order.getSymbol()){
            order = orderMap.get(orderId);
            if(order == null){
                throw new OrderCanNotBeModifiedNowException("order can not be modified now.");
            }
            OrderBook orderBook = order.getOrdeType() == OrderType.BUY
                    ? buyerOrderbook.get(order.getSymbol()) : sellerOrderbook.get(order.getSymbol());
            orderBook.removeOrder(order);
        }
    }

    private synchronized void removeExpiredOrders() {
        for(Map.Entry<String, BookOrder> bookOrderEntry : orderMap.entrySet()){
            BookOrder bookOrder = bookOrderEntry.getValue();
            if(bookOrder.isExpired()){
                OrderBook orderBook = bookOrder.getOrdeType() == OrderType.BUY
                        ? buyerOrderbook.get(bookOrder.getSymbol()) : sellerOrderbook.get(bookOrder.getSymbol());
                orderBook.removeOrder(bookOrder);
                orderMap.remove(bookOrder.getId());
                notifyCancelOrder(bookOrder);
            }
        }
    }

    private void notifyCancelOrder(BookOrder order){
        for(CancelledOrderObserver observer : cancelledOrderObservers){
            observer.update(order.getId());
        }
    }

    private void notifyTrades(List<Trade> trades){
        for(Trade trade: trades){
            for(TradeObserver observer : observers){
                observer.update(trade);
            }
        }
    }

    public void printOrderBooks() {
        buyerOrderbook.forEach((stockSymbol, orderBook) -> {
            System.out.println("Buy Order book for " + stockSymbol);
            System.out.println("Buy Orders:");
            BuyerOrderBook orderBook1 = (BuyerOrderBook) orderBook;
            orderBook1.getBuyOrders().forEach((price, orders) -> orders.forEach(System.out::println));
        });

        sellerOrderbook.forEach((stockSymbol, orderBook) -> {
            System.out.println("Sell Order book for " + stockSymbol);
            System.out.println("Sell Orders:");
            SellerOrderBook orderBook1 = (SellerOrderBook) orderBook;
            orderBook1.getSellOrders().forEach((price, orders) -> orders.forEach(System.out::println));
        });
    }
}
