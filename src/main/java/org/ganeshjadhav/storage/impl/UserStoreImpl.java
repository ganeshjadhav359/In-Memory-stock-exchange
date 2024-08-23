package org.ganeshjadhav.storage.impl;

import org.ganeshjadhav.model.User;
import org.ganeshjadhav.storage.BaseStore;

import java.util.HashMap;
import java.util.Map;

public class UserStoreImpl implements BaseStore<String, User> {

    private final Map<String, User> userMap;

    public UserStoreImpl() {
        userMap = new HashMap<>();
    }

    @Override
    public User put(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(String id) {
        return userMap.get(id);
    }

    @Override
    public void delete(String id) {
        userMap.remove(id);
    }
}
