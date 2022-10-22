package com.github.rskupnik.edgar.domain;

import com.github.rskupnik.edgar.db.entity.DashboardEntity;

import java.util.List;
import java.util.stream.Collectors;

public class Dashboard {

    private final List<Tile> tiles;

    public Dashboard(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public static Dashboard fromEntity(DashboardEntity entity) {
        return new Dashboard(
                entity.getTiles().stream().map(Tile::fromEntity).collect(Collectors.toList())
        );
    }

    public List<Tile> getTiles() {
        return tiles;
    }
}
