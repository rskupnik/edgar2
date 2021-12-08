package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.Edgar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.regex.Pattern;

@Component
public class DashboardLoader {

    private static final Logger logger = LoggerFactory.getLogger(DashboardLoader.class);
    private static final Pattern FILENAME_PATTERN = Pattern.compile("[/\\\\]?(\\w*)\\..*$");

    private final Edgar edgar;
    private final ApplicationArguments args;

    public DashboardLoader(Edgar edgar, ApplicationArguments args) {
        this.edgar = edgar;
        this.args = args;
    }

    @PostConstruct
    private void loadDashboards() {
        var optionValues = args.getOptionValues("dashboard");
        if (optionValues == null)
            return;

        optionValues.forEach(v -> {
            logger.info("Loading dashboard: " + v);
            var matcher = FILENAME_PATTERN.matcher(v);
            matcher.find();
            var name = matcher.group(1);
            edgar.loadDashboard(name, v);
        });
    }
}
