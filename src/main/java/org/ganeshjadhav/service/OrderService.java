package org.ganeshjadhav.service;

import org.ganeshjadhav.exception.InsufficientFundsException;
import org.ganeshjadhav.exception.OrderNotFound;
import org.ganeshjadhav.matchingEngine.MatchingEngine;
import org.ganeshjadhav.model.*;
import org.ganeshjadhav.storage.TradeStore;
import org.ganeshjadhav.storage.WalletStore;
import org.ganeshjadhav.storage.impl.OrderStoreImpl;

import java.util.List;

public class OrderService {

    private final OrderStoreImpl orderStore;
    private final WalletStore walletStore;
    private final TradeStore tradeStore;
    private final MatchingEngine matchingEngine;

    public OrderService(OrderStoreImpl orderStore, WalletStore walletStore, TradeStore tradeStore, MatchingEngine matchingEngine) {
        this.orderStore = orderStore;
        this.walletStore = walletStore;
        this.tradeStore = tradeStore;
        this.matchingEngine = matchingEngine;
    }

    public Order create(String userId, String symbol,double price, double quantity, OrderType orderType){
            Order order = Order.buildOrder(userId, symbol, orderType, price, quantity);
        try {
            order.setOrderStatus(OrderStatus.ACCEPTED);
            order.setTimestamp();
            double amountToDeduct = getAmountToDeduct(order);

            Wallet wallet = getOrCreateWallet(order);
            wallet.deductBalance(amountToDeduct);


            order = orderStore.put(order);
            List<Trade> trades = matchingEngine.addOrder(order.getId(), order.getSymbol(), order.getOrderType(),
                    order.getQuantity(), order.getPrice(),order.getTimestamp(), order.getTtl());

            saveTrade(trades);

            wallet = walletStore.put(wallet);

        }catch (Exception exception){
            order.setOrderStatus(OrderStatus.REJECTED);
            order = orderStore.put(order);
            System.out.println("order has been rejected..." + exception.getMessage());
            throw exception;
        }

        return order;
    }

    public Order get(String orderId){
       return orderStore.get(orderId);
    }

    private Wallet getOrCreateWallet(Order order){
        Wallet baseCurrencyWallet = null;
        Wallet targetCurrencyWallet = null;
        targetCurrencyWallet = walletStore.getUserCurrencyWallet(order.getUserId(), order.getSymbol());
        baseCurrencyWallet = walletStore.getUserCurrencyWallet(order.getUserId(), "INR");

        if(order.getOrderType() == OrderType.BUY){
            return baseCurrencyWallet;
        }

        if(targetCurrencyWallet == null){
            targetCurrencyWallet = walletStore.put(new Wallet(order.getUserId(), order.getSymbol()));
        }

        return targetCurrencyWallet;
    }

    public Order update(String orderId, double qty, double price){

        Order order = orderStore.get(orderId);
        if(order == null)
            throw new OrderNotFound("Order not found exception");
        Wallet wallet = getOrCreateWallet(order);
        if(order.getQuantity() + wallet.getBalance() < qty){
            throw new InsufficientFundsException("Funds not available.");
        }

        double originalAmount = 0;
        double newAmount = 0;

        if(order.getOrderType() == OrderType.BUY){
            originalAmount = order.getQuantity() * order.getPrice();
            newAmount = qty * price;
        }else {
            originalAmount = order.getQuantity();
            newAmount = qty;
        }



        List<Trade> trades = matchingEngine.updateOrder(orderId, order.getQuantity(), qty, price);
        wallet.addBalance(originalAmount);
        wallet.deductLockedBalance(originalAmount);
        wallet.deductBalance(newAmount);
        walletStore.put(wallet);

        order.setQuantity(qty);
        order.setPrice(price);
        order.setTimestamp();
        orderStore.put(order);

        saveTrade(trades);
        return orderStore.get(orderId);
    }

    public Order cancel(String orderId){
        Order order = orderStore.get(orderId);
        if(order == null)
            throw new OrderNotFound("Order not found exception");

        matchingEngine.cancelOrder(orderId);
        cancelOrder(orderId);
        return orderStore.get(orderId);
    }

    private double getAmountToDeduct(Order order){
        double amountToDeduct = 0;
        if(order.getOrderType() == OrderType.SELL){
            amountToDeduct = order.getQuantity();

        }else{
            amountToDeduct = order.getQuantity() * order.getPrice();
        }
        return amountToDeduct;
    }

    private void saveTrade(List<Trade> trades){
        for(Trade trade : trades){
            tradeStore.put(trade);
            executeTrade(trade);
        }
    }
    public void executeTrade(Trade trade){
        executeBuyOrder(orderStore.get(trade.getBuyerOrderId()), trade);
        executeSellOrder(orderStore.get(trade.getSellerOrderId()), trade);
    }

    public void executeBuyOrder(Order order, Trade trade){
        synchronized (this){
            Wallet targetCurrencyWallet = walletStore.getUserCurrencyWallet(order.getUserId(), order.getSymbol());
            Wallet baseCurrencyWallet = walletStore.getUserCurrencyWallet(order.getUserId(), "INR");
            targetCurrencyWallet.addBalance(trade.tradedQuantity);
            baseCurrencyWallet.deductLockedBalance(trade.getTradedQuantity() * trade.getPrice());

            order.setFilledQty(order.getFilledQty() + trade.getTradedQuantity());

            if(order.getFilledQty() == order.getQuantity()){
                order.setOrderStatus(OrderStatus.FILLED);
            }else{
                order.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
            }

            walletStore.put(baseCurrencyWallet);
            walletStore.put(targetCurrencyWallet);
            orderStore.put(order);
        }
    }

    public void executeSellOrder(Order order, Trade trade){
        synchronized (this){
            Wallet baseCurrencyWallet = walletStore.getUserCurrencyWallet(order.getUserId(), order.getSymbol());
            Wallet targetCurrencyWallet = walletStore.getUserCurrencyWallet(order.getUserId(), "INR");
            baseCurrencyWallet.deductLockedBalance(trade.getTradedQuantity());
            targetCurrencyWallet.addBalance(trade.getTradedQuantity() * trade.getPrice());

            order.setFilledQty( order.getFilledQty() + trade.getTradedQuantity());

            if(order.getFilledQty() == order.getQuantity()){
                order.setOrderStatus(OrderStatus.FILLED);
            }else{
                order.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
            }

            walletStore.put(baseCurrencyWallet);
            walletStore.put(targetCurrencyWallet);
            orderStore.put(order);
        }
    }

    public void cancelOrder(String orderId){
        synchronized (this){
            Order order = orderStore.get(orderId);

            if(order == null){
                return;
            }

            Wallet baseCurrencyWallet;
            if(order.getOrderType() == OrderType.BUY){
                baseCurrencyWallet = walletStore.getUserCurrencyWallet(order.getUserId(), "INR");
                baseCurrencyWallet.addBalance((order.getQuantity() - order.getFilledQty()) * order.getPrice());
                baseCurrencyWallet.deductLockedBalance((order.getQuantity() - order.getFilledQty()) * order.getPrice());
            }else{
                baseCurrencyWallet = walletStore.getUserCurrencyWallet(order.getUserId(), order.getSymbol());
                baseCurrencyWallet.addBalance(order.getQuantity() - order.getFilledQty());
                baseCurrencyWallet.deductLockedBalance(order.getQuantity() - order.getFilledQty());
            }

            order.setOrderStatus(OrderStatus.CANCELED);
            walletStore.put(baseCurrencyWallet);
            orderStore.put(order);
        }
    }
}
