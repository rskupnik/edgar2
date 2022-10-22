package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.Edgar;
import com.github.rskupnik.edgar.domain.Dashboard;
import com.github.rskupnik.edgar2.web.dto.DashboardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class DashboardController {

    private final Edgar edgar;

    @Autowired
    public DashboardController(Edgar edgar) {
        this.edgar = edgar;
    }

    @GetMapping("dashboards/{id}")
    public DashboardDto getDashboard(@PathVariable("id") String id) {
        return DashboardDto.fromDomainClass(edgar.getDashboard(id).orElse(new Dashboard(Collections.emptyList())));
    }
}
