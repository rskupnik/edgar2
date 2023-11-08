package com.github.rskupnik.edgar.db;

import java.util.Optional;

public interface CRUDRepository<K, V> extends Repository<K, V> {

    Optional<V> find(K key);
    void save(K key, V value);
    void delete(K key);
}
