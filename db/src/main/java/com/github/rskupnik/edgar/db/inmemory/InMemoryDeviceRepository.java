package com.github.rskupnik.edgar.db.inmemory;

import com.github.rskupnik.edgar.db.AllEntitiesRepository;
import com.github.rskupnik.edgar.db.entity.DeviceEntity;
import com.github.rskupnik.edgar.db.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryDeviceRepository extends InMemoryEntityRepository<String, DeviceEntity> implements DeviceRepository, AllEntitiesRepository<String, DeviceEntity> {

    @Override
    public List<DeviceEntity> findAll() {
        return new ArrayList<>(entities.values());
    }
}
