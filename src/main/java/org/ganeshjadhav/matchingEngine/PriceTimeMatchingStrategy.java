package org.ganeshjadhav.matchingEngine;

import org.ganeshjadhav.matchingEngine.model.BookOrder;
import org.ganeshjadhav.matchingEngine.store.OrderBook;
import org.ganeshjadhav.model.OrderType;
import org.ganeshjadhav.model.Trade;

import java.util.List;

public class PriceTimeMatchingStrategy implements MatchingStrategy {
    @Override
    public void match(BookOrder order, OrderBook oppositeOrderBook, List<Trade> trades) {
        while (order.getQty() > 0) {
            BookOrder bestOppositeOrder = oppositeOrderBook.getBestOrder();
            if (bestOppositeOrder == null || (order.getOrdeType() == OrderType.BUY && order.getPrice() < bestOppositeOrder.getPrice()) ||
                    (order.getOrdeType() == OrderType.SELL && order.getPrice() > bestOppositeOrder.getPrice())) {
                break;
            }

            double tradedQuantity = Math.min(order.getQty(), bestOppositeOrder.getQty());
            String buyerOrderId;
            String sellerOrderId;

            if(order.getOrdeType() == OrderType.BUY){
                buyerOrderId = order.getId();
                sellerOrderId = bestOppositeOrder.getId();
            }else{
                buyerOrderId = bestOppositeOrder.getId();
                sellerOrderId = order.getId();
            }

            Trade trade = new Trade(order.getSymbol(), bestOppositeOrder.getPrice(), tradedQuantity, buyerOrderId, sellerOrderId);
            trades.add(trade);

            order.setQty(order.getQty() - tradedQuantity);
            bestOppositeOrder.setQty(bestOppositeOrder.getQty() - tradedQuantity);

            if (bestOppositeOrder.getQty() == 0) {
                oppositeOrderBook.removeOrder(bestOppositeOrder);
            }
        }
    }
}
