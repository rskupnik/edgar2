package com.github.rskupnik.edgar.db.inmemory;

import com.github.rskupnik.edgar.db.AllEntitiesRepository;
import com.github.rskupnik.edgar.db.entity.DeviceDataEntity;
import com.github.rskupnik.edgar.db.repository.DeviceDataRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryDeviceDataRepository extends InMemoryEntityRepository<String, DeviceDataEntity> implements DeviceDataRepository, AllEntitiesRepository<String, DeviceDataEntity> {

    @Override
    public List<DeviceDataEntity> findAll() {
        return new ArrayList<>(entities.values());
    }
}
