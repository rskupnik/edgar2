package com.github.rskupnik.edgar2.web.dto;

import com.github.rskupnik.edgar.domain.Dashboard;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardDto {

    private List<TileDto> tiles;

    public DashboardDto() {

    }

    public Dashboard toDomainClass() {
        return new Dashboard(tiles.stream().map(TileDto::toDomainClass).collect(Collectors.toList()));
    }

    public static DashboardDto fromDomainClass(Dashboard dashboard) {
        var dto = new DashboardDto();
        dto.setTiles(dashboard.getTiles().stream().map(TileDto::fromDomainClass).collect(Collectors.toList()));
        return dto;
    }

    public List<TileDto> getTiles() {
        return tiles;
    }

    public void setTiles(List<TileDto> tiles) {
        this.tiles = tiles;
    }
}
