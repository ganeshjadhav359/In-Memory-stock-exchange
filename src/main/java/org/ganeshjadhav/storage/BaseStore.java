package org.ganeshjadhav.storage;

public interface BaseStore<Key, Value> {
    Value put(Value value);
    Value get(Key key);
    void delete(Key key);
}
