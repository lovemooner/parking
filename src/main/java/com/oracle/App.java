package com.oracle;

import com.oracle.model.Car;
import com.oracle.model.ParkingInfo;
import com.oracle.pojo.CacheAdapter;
import com.oracle.service.HttpUtil;
import com.oracle.service.IWorker;
import com.oracle.service.ParkingService;
import com.oracle.service.Worker;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class App {
    public static final Logger LOG = LoggerFactory.getLogger(App.class);
    ParkingService service = new ParkingService();

    public void startup() {
        LOG.info("Fetch Parking Info Startup... ");
        CacheAdapter.setCarList(service.requestCarList());
        service.startCacheCarThread();
        service.reBuildParkingMap();
        IWorker worker = new Worker();
        worker.doWork();
    }

    public static void main(String[] args) {
        App app = new App();
        app.startup();


    }

}
