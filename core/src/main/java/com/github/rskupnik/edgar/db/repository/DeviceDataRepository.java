package com.github.rskupnik.edgar.db.repository;

import com.github.rskupnik.edgar.db.AllEntitiesRepository;
import com.github.rskupnik.edgar.db.CRUDRepository;
import com.github.rskupnik.edgar.db.entity.DeviceDataEntity;

public interface DeviceDataRepository extends CRUDRepository<String, DeviceDataEntity>, AllEntitiesRepository<String, DeviceDataEntity> {
}
