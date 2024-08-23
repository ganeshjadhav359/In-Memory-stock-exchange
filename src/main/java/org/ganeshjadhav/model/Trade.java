package org.ganeshjadhav.model;

import lombok.Data;
import lombok.ToString;
import org.ganeshjadhav.utils.Common;

@Data
@ToString
public class Trade {
    public String id;
    public String symbol;
    public String buyerOrderId;
    public String sellerOrderId;
    public double tradedQuantity;
    public double price;
    private long timestamp;

    public Trade(String symbol, double price, double tradedQuantity, String buyerOrderId, String sellerOrderId) {
        this.id = Common.getUUID();
        this.symbol = symbol;
        this.buyerOrderId = buyerOrderId;
        this.sellerOrderId = sellerOrderId;
        this.tradedQuantity = tradedQuantity;
        this.price = price;
        this.timestamp = System.currentTimeMillis();
    }
}
