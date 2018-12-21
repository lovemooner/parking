package com.oracle;

import com.oracle.service.ParkingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static final Logger LOG = LoggerFactory.getLogger(App.class);
    private ParkingService service = new ParkingService();


    public void startup() {
        LOG.info("Fetch Parking Info Startup");
        service.initCarCache();
        service.reBuildParkingMap();
        service.doWork();
    }

    public static void main(String[] args) {
        App app = new App();
        app.startup();
    }
}
