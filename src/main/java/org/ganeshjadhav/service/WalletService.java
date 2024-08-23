package org.ganeshjadhav.service;

import org.ganeshjadhav.exception.NotFoundException;
import org.ganeshjadhav.model.Wallet;
import org.ganeshjadhav.storage.WalletStore;

public class WalletService {

    private final WalletStore walletStore;

    public WalletService(WalletStore walletStore) {
        this.walletStore = walletStore;
    }

    public void create(String userId, String symbol){
        if(walletStore.getUserCurrencyWallet(userId, symbol) == null){
            walletStore.put(new Wallet(userId, symbol));
        }
    }

    public Wallet addBalance(String userId, double amount, String symbol){

        Wallet wallet = walletStore.getUserCurrencyWallet(userId, symbol);
        if(wallet == null) {
           wallet =  new Wallet(userId, symbol);
        }
        wallet.addBalance(amount);
        walletStore.put(wallet);
        return wallet;
    }

    public Wallet getUserCurrencyWallet(String userId, String symbol){
        Wallet wallet = walletStore.getUserCurrencyWallet(userId, symbol);
        if(wallet == null)
            throw new NotFoundException("Wallet not found.");
        return wallet;
    }
}
