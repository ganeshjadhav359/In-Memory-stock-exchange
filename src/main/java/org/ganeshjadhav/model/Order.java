package org.ganeshjadhav.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.ganeshjadhav.utils.Common;

@Data
@Builder
@ToString
public class Order {
    private String id;
    private String userId;
    private OrderType orderType;
    private OrderStatus orderStatus;
    private String symbol;
    private double quantity;
    private double filledQty;
    private double price;
    private double avgPrice;
    private long timestamp;
    private long ttl;

    public void setTimestamp(){
        this.timestamp = System.currentTimeMillis();
    }

    public static Order buildOrder(String userId , String symbol, OrderType orderType , double price , double quantity){
        return Order.builder()
                .id(Common.getUUID())
                .symbol(symbol)
                .orderType(orderType)
                .userId(userId)
                .price(price)
                .quantity(quantity)
                .ttl(Common.getDefaultTTl())
                .build();
    }
}
