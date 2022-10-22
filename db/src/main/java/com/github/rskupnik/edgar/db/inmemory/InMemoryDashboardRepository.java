package com.github.rskupnik.edgar.db.inmemory;

import com.github.rskupnik.edgar.db.entity.DashboardEntity;
import com.github.rskupnik.edgar.db.repository.DashboardRepository;

public class InMemoryDashboardRepository extends InMemoryEntityRepository<String, DashboardEntity> implements DashboardRepository {
}
