package org.ganeshjadhav.storage.impl;


import org.ganeshjadhav.model.Wallet;
import org.ganeshjadhav.storage.WalletStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WalletStoreImpl implements WalletStore {
    private final ConcurrentHashMap<String, Wallet> wallets;
    private final Map<String, Wallet> currencyUserWallet;

    public WalletStoreImpl() {
        wallets = new ConcurrentHashMap<>();
        currencyUserWallet = new HashMap<>();
    }

    @Override
    public Wallet put(Wallet wallet) {
        wallets.put(wallet.getId(), wallet);
        currencyUserWallet.put(wallet.getUserId()+ "-" + wallet.getSymbol(), wallet);

        return wallet;
    }

    @Override
    public Wallet get(String walletId) {
        return wallets.get(walletId);
    }

    @Override
    public void delete(String id) {
        wallets.remove(id);
    }

    @Override
    public Wallet getUserCurrencyWallet(String userId, String symbol){
        return currencyUserWallet.get(userId+ "-" + symbol);
    }

}
