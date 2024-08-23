package org.ganeshjadhav.model;

import lombok.Data;
import lombok.ToString;
import org.ganeshjadhav.exception.InsufficientFundsException;
import org.ganeshjadhav.utils.Common;

@Data
@ToString
public class Wallet {
    private String id;
    private String userId;
    private String symbol;
    private double balance;
    private double lockedBalance;
    public Wallet(String userId, String symbol){
        this.id = Common.getUUID();
        this.userId = userId;
        this.symbol = symbol;
    }

    public void deductLockedBalance(double amount){
        lockedBalance -= amount;
    }

    public void addLockedBalance(double amount){
        lockedBalance += amount;
    }

    public void addBalance(double amount){
        balance += amount;
    }

    public void deductBalance(double amount){
        if(balance < amount){
            throw new InsufficientFundsException("Funds are not available");
        }
        balance -= amount;
        addLockedBalance(amount);
    }
}
