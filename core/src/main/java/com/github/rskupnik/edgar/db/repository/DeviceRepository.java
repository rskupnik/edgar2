package com.github.rskupnik.edgar.db.repository;

import com.github.rskupnik.edgar.db.AllEntitiesRepository;
import com.github.rskupnik.edgar.db.CRUDRepository;
import com.github.rskupnik.edgar.db.entity.DeviceEntity;

public interface DeviceRepository extends CRUDRepository<String, DeviceEntity>, AllEntitiesRepository<String, DeviceEntity> {

}
