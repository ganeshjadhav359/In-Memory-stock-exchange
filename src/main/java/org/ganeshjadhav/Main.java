package org.ganeshjadhav;

import org.ganeshjadhav.matchingEngine.MatchingEngine;
import org.ganeshjadhav.model.*;
import org.ganeshjadhav.observer.CancelledOrderObserver;
import org.ganeshjadhav.observer.TradeObserver;
import org.ganeshjadhav.service.CurrencyService;
import org.ganeshjadhav.service.OrderService;
import org.ganeshjadhav.service.UserService;
import org.ganeshjadhav.service.WalletService;
import org.ganeshjadhav.storage.CurrencyStore;
import org.ganeshjadhav.storage.TradeStore;
import org.ganeshjadhav.storage.WalletStore;
import org.ganeshjadhav.storage.impl.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final MatchingEngine matchingEngine = new MatchingEngine();

    // create stores
    private static final CurrencyStore currencyStore = new CurrencyStoreImpl();
    private static final TradeStore tradeStore = new TradeStoreImpl();
    private static final WalletStore walletStore = new WalletStoreImpl();
    private static final OrderStoreImpl orderStore = new OrderStoreImpl();
    private static final UserStoreImpl userStore = new UserStoreImpl();

    // create services
    private static final CurrencyService currencyService = new CurrencyService(currencyStore, matchingEngine);
    private static final  OrderService orderService = new OrderService(orderStore, walletStore, tradeStore, matchingEngine);
    private static final WalletService walletService = new WalletService(walletStore);
    private static final UserService userService = new UserService(userStore, walletService);

    private final static List<User> userList = new ArrayList<>();
    private final static List<Wallet> wallets = new ArrayList<>();
    private final static List<Stock> currencies = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        printMatchingEngin();

        // create dummy users1
        createUsers();
        User user1 = userList.get(0);
        User user2 = userList.get(1);
        User user3 = userList.get(2);

        // create currencies
        createCurrencies(List.of("RIL","IRFC", "HDFC"));

        // setup wallets
        setupWalletServiceData();
        printWallets();

        // setup observers for matching engine
        TradeObserver tradeObserver = new TradeObserver();
        CancelledOrderObserver cancelledOrderObserver = new CancelledOrderObserver(orderService);
        matchingEngine.addObserver(tradeObserver);
        matchingEngine.addCancelledOrderObserver(cancelledOrderObserver);


        // create orders
        Order order1 = orderService.create(user1.getId(),"RIL", 100, 10, OrderType.BUY);
        Order order2 = orderService.create(user3.getId(),"RIL", 90, 5, OrderType.SELL);
        orderService.cancel(order1.getId());
        
        Order order3 = orderService.create(user2.getId(),"IRFC", 110, 10, OrderType.SELL);
        Order order4 = orderService.create(user3.getId(),"RIL", 100, 5, OrderType.SELL);

        System.out.println(orderService.get(order1.getId()));
        System.out.println(orderService.get(order2.getId()));
        System.out.println(orderService.get(order4.getId()));




        printMatchingEngin();
        printWallets();

        try {

            Thread.sleep(2000);
            order2 =  orderService.cancel(order2.getId());
            printMatchingEngin();
            printWallets();

            Thread.sleep(2000);
            order2 = orderService.create(user3.getId(),"RIL", 110, 10, OrderType.SELL);
            printMatchingEngin();
            printWallets();

            Thread.sleep(2000);
            order2 = orderService.update(order2.getId(),10, 100);
            printMatchingEngin();
            printWallets();

            Thread.sleep(15000); // Wait 6 seconds to allow orders to expire
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        printMatchingEngin();
        System.out.println(orderService.get(order1.getId()));
        System.out.println(orderService.get(order2.getId()));
        printWallets();
    }

    private static void createCurrencies(List<String> symbols) throws Exception {
        System.out.println("******************* print currencies start  ***************");
        System.out.println();
        for(String symbol : symbols){
            Stock stockOutput = currencyService.create(symbol + " name", symbol, 1, 100);
            System.out.println(stockOutput);
            currencies.add(stockOutput);
        }
        System.out.println();
        System.out.println("******************* print currencies end  ***************");
        System.out.println();
    }

    private static void createUsers(){
        System.out.println("******************* print users start  ***************");
        System.out.println();

        for(int i = 0; i < 4; i++){
            User user = userService.create("user"+i, "user"+i+"@gmai.com", "888"+ i +"99");
            userList.add(user);
            System.out.println(user);
        }

        System.out.println();
        System.out.println("******************* print users end  ***************");
        System.out.println();
    }

    private static void  printMatchingEngin(){
        System.out.println("******************* print order book start  ***************");
        System.out.println();
        matchingEngine.printOrderBooks();
        System.out.println();
        System.out.println("******************* print order book end  ***************");
        System.out.println();
    }

    private static void setupWalletServiceData(){
        for(int i = 0 ; i < userList.size(); i++){
            User user = userList.get(i);
            Wallet wallet1;
            Wallet wallet2;

            if(i % 2 == 0){
                wallet1 = walletService.addBalance(user.getId(), 1000, "INR");
                wallet2 = walletService.addBalance(user.getId(), 100, "RIL");
            }else{
                wallet1 = walletService.addBalance(user.getId(), 100, "IRFC");
                wallet2 = walletService.addBalance(user.getId(), 100, "HDFC");
            }
            wallets.add(wallet1);
            wallets.add(wallet2);
        }
    }

    private static void printWallets(){
        System.out.println("******************* print wallet  start  ***************");
        System.out.println();
        for(Wallet wallet : wallets){
            System.out.println(walletService.getUserCurrencyWallet(wallet.getUserId(), wallet.getSymbol()));
        }
        System.out.println();
        System.out.println("******************* print wallet  end  ***************");
        System.out.println();
    }
}