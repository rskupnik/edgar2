package com.github.rskupnik.edgar.db;

import java.util.List;

public interface AllEntitiesRepository<K, V> extends Repository<K, V> {
    List<V> findAll();
}
