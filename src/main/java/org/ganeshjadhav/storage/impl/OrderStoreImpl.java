package org.ganeshjadhav.storage.impl;

import org.ganeshjadhav.model.Order;
import org.ganeshjadhav.storage.BaseStore;

import java.util.HashMap;
import java.util.Map;

public class OrderStoreImpl implements BaseStore<String, Order> {
    private final Map<String, Order> orderMap;

    public OrderStoreImpl() {
        orderMap = new HashMap<>();
    }
    @Override
    public Order put(Order order) {
        orderMap.put(order.getId(), order);
        return order;
    }

    @Override
    public Order get(String id) {
        return orderMap.get(id);
    }

    @Override
    public void delete(String id) {
       orderMap.remove(id);
    }
}
