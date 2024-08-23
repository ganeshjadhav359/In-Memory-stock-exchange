package org.ganeshjadhav.model;


import lombok.Builder;
import lombok.Data;
import org.ganeshjadhav.utils.Common;

@Data
@Builder
public class Stock {
    private String id;
    private String name;
    private String symbol;
    private double minQty;
    private double maxQty;

    public static Stock build(String name, String symbol, double minQty , double maxQty){
        return Stock.builder()
                .id(Common.getUUID())
                .name(name)
                .symbol(symbol)
                .minQty(minQty)
                .maxQty(maxQty)
                .build();
    }
}
