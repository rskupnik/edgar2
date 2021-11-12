package com.github.rskupnik.edgar.db.entity;

import com.github.rskupnik.edgar.domain.Dashboard;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardEntity implements DbEntity {

    private List<TileEntity> tiles;

    public DashboardEntity() {}

    public DashboardEntity(List<TileEntity> tiles) {
        this.tiles = tiles;
    }

    public static DashboardEntity fromDomainObject(Dashboard dashboard) {
        return new DashboardEntity(
                dashboard.getTiles().stream().map(TileEntity::fromDomainObject).collect(Collectors.toList())
        );
    }

    public List<TileEntity> getTiles() {
        return tiles;
    }

    public void setTiles(List<TileEntity> tiles) {
        this.tiles = tiles;
    }
}
