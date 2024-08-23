package org.ganeshjadhav.matchingEngine.model;

import lombok.Data;
import lombok.ToString;
import org.ganeshjadhav.model.OrderType;

@Data
@ToString
public class BookOrder {
    private String id;
    private String symbol;
    private OrderType ordeType;
    private double qty;
    private double price;
    private long timestamp;
    private long ttl;

    public BookOrder(String orderId, String symbol, OrderType ordeType, double qty, double price, long timestamp, long ttl) {
        this.id = orderId;
        this.symbol = symbol;
        this.ordeType = ordeType;
        this.qty = qty;
        this.price = price;
        this.timestamp = timestamp;
        this.ttl = ttl;
    }

    public boolean isExpired() {
//        System.out.println(this);
        return System.currentTimeMillis() - timestamp >= ttl;
    }
}
