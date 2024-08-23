package org.ganeshjadhav.storage;

import org.ganeshjadhav.model.Wallet;

import java.util.List;

public interface WalletStore extends BaseStore<String, Wallet>{

    Wallet getUserCurrencyWallet(String userId, String symbol);
}
