package com.github.rskupnik.edgar.db.inmemory;

import com.github.rskupnik.edgar.db.CRUDRepository;
import com.github.rskupnik.edgar.db.entity.DbEntity;

import java.util.HashMap;
import java.util.Optional;

public abstract class InMemoryEntityRepository<K, E extends DbEntity> implements CRUDRepository<K, E> {

    protected final HashMap<K, E> entities = new HashMap<>();

    @Override
    public Optional<E> find(K key) {
        return Optional.ofNullable(entities.get(key));
    }

    @Override
    public void save(K key, E value) {
        entities.put(key, value);
    }

    @Override
    public void delete(K key) {
        entities.remove(key);
    }
}
