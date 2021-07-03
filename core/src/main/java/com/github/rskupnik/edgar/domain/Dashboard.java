package com.github.rskupnik.edgar.domain;

import java.util.List;

public class Dashboard {

    private final List<Tile> tiles;

    public Dashboard(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public List<Tile> getTiles() {
        return tiles;
    }
}
