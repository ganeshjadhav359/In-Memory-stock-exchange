package org.ganeshjadhav.observer;

import org.ganeshjadhav.service.OrderService;

public class CancelledOrderObserver {
    private final OrderService orderService;
    public CancelledOrderObserver(OrderService orderService) {
        this.orderService = orderService;
    }
    public void update(String orderId){
        orderService.cancelOrder(orderId);
    }
}
