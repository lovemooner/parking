package com.oracle.service;

import com.oracle.model.Car;
import com.oracle.model.ParkingInfo;
import com.oracle.pojo.*;
import com.oracle.util.CacheAdapter;
import com.oracle.util.HttpUtil;
import com.oracle.util.ThreadPool;
import love.moon.common.HttpResponse;
import love.moon.util.DateUtil;
import love.moon.util.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class ParkingService {
    public static final Logger LOG = LoggerFactory.getLogger(ParkingService.class);

    private ExecutorService pool = ThreadPool.newThreadPool();


    public void startCacheCarThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpResponse response = HttpUtil.sendGet(Constants.URL_GET_CAR_COUNT);
                    Counter counter = JsonUtil.jsonToObj(response.getContent(), Counter.class);
                    if (counter.getCount() != 0 && counter.getCount() != CacheAdapter.get().size()) {
                        LOG.info("startCacheCarThread-->update Car list");
                        CacheAdapter.set(requestCarList());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1 * 60 * 1000l);
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }).start();
    }

    public void initCarCache() {
        LOG.info("initCarCache");
        CacheAdapter.set(requestCarList());
        startCacheCarThread();
    }

    public void reBuildParkingMap() {
        LOG.info("reBuildParkingMap");
        List<GetCurrentParkingResult.CurrentParkingInfo> parkingInfoList = requestCurrentParking();
        if (CollectionUtils.isNotEmpty(parkingInfoList)) {
            for (GetCurrentParkingResult.CurrentParkingInfo parkingInfo : parkingInfoList) {
                CacheAdapter.getParkingMap().put(parkingInfo.getCarId(), parkingInfo.getId());
            }
        }
    }


    public void doWork() {
        while (true) {
            try {
                if (isTaskDone(pool)) {
                    LOG.info("star new round Fetch");
                    for (Car car : CacheAdapter.get()) {
                        pool.submit(new Worker(car));
                    }
                    if(Constants.DEBUG){
                        ThreadPool.startMonitor(pool, 1000l);
                    }
                    continue;
                }
                long sleepTime = getSleepTime();
                LOG.info("Thread Pool will sleep {} ms", sleepTime);
                Thread.sleep(sleepTime);

            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 根据不同时段
     *
     * @throws InterruptedException
     */
    private long getSleepTime() {
        long time = System.currentTimeMillis();
        if (time > DateUtil.getTimeMillisByHourOfDay(16) && time < DateUtil.getTimeMillisByHourOfDay(20)) { //16:00:00~20:00:00
            return Constants.AFTER_WORK_SLEEP_TIME;
        } else if (time >= DateUtil.getTimeMillisByHourOfDay(6) && time <= DateUtil.getTimeMillisByHourOfDay(16)) {
            return Constants.ON_WORK_SLEEP_TIME;
        } else {
            return Constants.SLEEP_TIME_DEFAULT;
        }
    }


    /**
     * @param pool
     * @return
     */
    private boolean isTaskDone(ExecutorService pool) {
        int activeCount = ((ThreadPoolExecutor) pool).getActiveCount();
        int queueSize = ((ThreadPoolExecutor) pool).getQueue().size();
        return queueSize == 0 && activeCount == 0;

    }

    public List<GetCurrentParkingResult.CurrentParkingInfo> requestCurrentParking() {
        try {
            HttpResponse response = HttpUtil.sendGet(Constants.URL_GET_CURRENT_PARKING);
            GetCurrentParkingResult result = JsonUtil.jsonToObj(response.getContent(), GetCurrentParkingResult.class);
            return result.getItems();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public List<Car> requestCarList() {
        LOG.info("sendGet car List ");
        try {
            HttpResponse response = HttpUtil.sendGet(Constants.URL_GET_CAR);
            GetCarResult result = JsonUtil.jsonToObj(response.getContent(), GetCarResult.class);
            LOG.info(JsonUtil.jsonStandardizing(JsonUtil.objectToJson(result)));
            return result.getItems();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }

    }

    public HttpResponse putParkingInfo(ParkingInfo parkingInfo) {
        if (parkingInfo.getId() == null || parkingInfo.getOutTime() == null) {
            LOG.error("error parking info:{}", JsonUtil.objectToJson(parkingInfo));
            return null;
        }
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(parkingInfo.getId()));
        map.put("outTime", parkingInfo.getOutTime());
        try {
            return HttpUtil.sendHttp(Constants.URL_PARKING_INFO, map, "PUT");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            HttpResponse response = new HttpResponse();
            response.setCode(0);
            return response;
        }
    }

    public HttpResponse postParkingInfo(ParkingInfo parkingInfo) {
        Map<String, String> map = new HashMap<>();
        map.put("comeTime", parkingInfo.getComeTime());
        map.put("lotName", parkingInfo.getLotName());
        map.put("carImg", String.valueOf(parkingInfo.getCarImg()));
        map.put("carId", String.valueOf(parkingInfo.getCarId()));
        try {
            return HttpUtil.sendHttp(Constants.URL_PARKING_INFO, map, "POST");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            HttpResponse response = new HttpResponse();
            response.setCode(0);
            return response;
        }
    }

    public ParkingInfo requestParkingInfo(String carNo) {
        LOG.debug("getParkingInfo");
//        System.setProperty("http.proxyHost", "cn-proxy.jp.oracle.com");
//        System.setProperty("http.proxyPort", "80");
        StringBuilder sb = new StringBuilder();
        sb.append("https://cloud.keytop.cn/service/parking/queryWithLotId?lotId=2870&lpn=")
                .append(carNo);
        String url = sb.toString();
        try {
            HttpResponse response = HttpUtil.sendGet(url);
            GetParamResult result = JsonUtil.jsonToObj(response.getContent(), GetParamResult.class);
            if ("success".equals(result.getType())) {
                String parkingQueryUrl = Constants.URL_PARING_QUERY + result.getData().getParams().split("&")[4];
                response = HttpUtil.sendGet(parkingQueryUrl);
                LOG.debug(response.getContent());
                GetParkingInfoResult parkingInfoResult = JsonUtil.jsonToObj(response.getContent(), GetParkingInfoResult.class);
                return parkingInfoResult.toParkingInfo();
            } else {
                LOG.debug(result.getTip());
                ParkingInfo parkingInfo = new ParkingInfo();
                return parkingInfo;
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }


}
