package com.oracle.service;

import com.oracle.pojo.Car;
import com.oracle.pojo.ParkingInfo;
import com.oracle.pojo.*;
import com.oracle.util.CacheAdapter;
import com.oracle.util.HttpUtil;
import com.oracle.util.ThreadPoolFactory;
import love.moon.common.HttpResponse;
import love.moon.util.DateUtil;
import love.moon.util.JsonUtil;
import love.moon.util.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class ParkingService {
    public static final Logger LOG = LoggerFactory.getLogger(ParkingService.class);

    private ThreadPoolExecutor pool = ThreadPoolFactory.newThreadPool();


    public void startCacheCarThread() {
        new Thread(() -> {
            try {
                HttpResponse response = HttpUtil.get(Constants.URL_GET_CAR_COUNT);
                Counter counter = JsonUtil.jsonToObj(response.getContent(), Counter.class);
                if (counter.getCount() != 0 && counter.getCount() != CacheAdapter.get().size()) {
                    LOG.info("startCacheCarThread-->update Car list");
                    CacheAdapter.set(httpGetCarList());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10 * 60 * 1000l);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }).start();
    }

    public void initCarCache() {
        LOG.info("initCarCache");
        CacheAdapter.set(httpGetCarList());
        startCacheCarThread();
    }

    public void reBuildParkingMap() {
        LOG.info("reBuildParkingMap");
        List<GetCurrentParkingResult.CurrentParkingInfo> parkingInfoList = httpGetParking();
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
                    LOG.info("star new round Fetch,Thread Pool coreSize:{}",Constants.POOL_CORE_SIZE);
                    CacheAdapter.get().forEach(car -> pool.submit(new Worker(car)));
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
     * @throws InterruptedException
     */
    private long getSleepTime() {
        long time = System.currentTimeMillis();
        if (time > DateUtil.getTimeMillisByHourOfDay(15) && time < DateUtil.getTimeMillisByHourOfDay(20)) {
            return Constants.AFTER_WORK_SLEEP_TIME;
        } else if (time >= DateUtil.getTimeMillisByHourOfDay(6) && time <= DateUtil.getTimeMillisByHourOfDay(15)) {
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

    public List<GetCurrentParkingResult.CurrentParkingInfo> httpGetParking() {
        try {
            HttpResponse response = HttpUtil.get(Constants.URL_GET_CURRENT_PARKING);
            GetCurrentParkingResult result = JsonUtil.jsonToObj(response.getContent(), GetCurrentParkingResult.class);
            return result.getItems();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public List<Car> httpGetCarList() {
        LOG.info("sendGet car List ");
        try {
            HttpResponse response = HttpUtil.get(Constants.URL_GET_CAR);
            GetCarResult result = JsonUtil.jsonToObj(response.getContent(), GetCarResult.class);
            LOG.info(JsonUtil.jsonStandardizing(JsonUtil.objectToJson(result)));
            return result.getItems();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return new ArrayList<>();
        }

    }

    public HttpResponse putParkingInfo(ParkingInfo parkingInfo) {
        if (parkingInfo.getId() == null || parkingInfo.getOutTime() == null) {
            LOG.error("putParkingInfo failed,error parking info:{}", JsonUtil.objectToJson(parkingInfo));
            return null;
        }
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(parkingInfo.getId()));
        map.put("outTime", parkingInfo.getOutTime());
        try {
            return HttpUtil.put(Constants.URL_PARKING_INFO, map);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return new HttpResponse(0);
        }
    }

    public HttpResponse postParkingInfo(ParkingInfo parkingInfo) {
        Map<String, String> map = new HashMap<>();
        map.put("comeTime", parkingInfo.getComeTime());
        map.put("lotName", parkingInfo.getLotName());
        map.put("carImg", String.valueOf(parkingInfo.getCarImg()));
        map.put("carId", String.valueOf(parkingInfo.getCarId()));
        try {
            return HttpUtil.post(Constants.URL_PARKING_INFO, map);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return new HttpResponse(0);
        }
    }

    public ParkingInfo httpGetParkingInfo(String carNo) {
        try {
            HttpResponse response = HttpUtil.get(Constants.URL_GET_PARKING_INFO + carNo);
            GetParamResult result = JsonUtil.jsonToObj(response.getContent(), GetParamResult.class);
            if ("success".equals(result.getType())) {
                String parkingQueryUrl = Constants.URL_PARING_QUERY + result.getData().getParams().split("&")[4];
                response = HttpUtil.get(parkingQueryUrl);
                GetParkingInfoResult parkingInfoResult = JsonUtil.jsonToObj(response.getContent(), GetParkingInfoResult.class);
                return parkingInfoResult.toParkingInfo();
            } else {
                LOG.debug("CarNo:{},tip:{}",carNo,result.getTip());
                return new ParkingInfo(true);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return new ParkingInfo(false);
        }
    }


     class Worker implements Runnable {

        private Car car;

        public Worker(Car car) {
            this.car = car;
        }

        @Override
        public void run() {
            ParkingInfo parkingInfo = httpGetParkingInfo(car.getCarNo());
            if(!parkingInfo.isSuccess()){
                return;
            }
            parkingInfo.setCarId(car.getId());
            if (notComming(parkingInfo)) {
                LOG.debug("Car:" + parkingInfo.getCarId() + ",employee is  not comming or does not driving car.");
                return;
            } else if (isSynchronized(parkingInfo)) {
                LOG.info("Car:" + parkingInfo.getCarId() + " is already synchronized.");
                return;
            } else if (isNewComming(parkingInfo)) {
                HttpResponse response = postParkingInfo(parkingInfo);
                if (response.getCode() == 200) {
                    Long parkingInfoId = NumberUtil.longValue(response.getContent());
                    if (parkingInfoId == null) {
                        LOG.error("Car has been synchronized,carId:{}", car.getId());
                    } else {
                        CacheAdapter.getParkingMap().put(parkingInfo.getCarId(), parkingInfoId);
                        LOG.info("Parking Info is saved,carId:{}", car.getId());
                    }
                }
            } else if (isOut(parkingInfo)) {
                parkingInfo.setOutTime(DateUtil.getChinaTime());
                parkingInfo.setId(CacheAdapter.getParkingMap().get(car.getId()));
                HttpResponse response = putParkingInfo(parkingInfo);
                if (response.getCode() == 200) {
                    CacheAdapter.getParkingMap().remove(car.getId());
                    LOG.info("Car is Out,carId:{}", car.getId());
                }
            }
        }


        /**
         * 车辆是否出场
         *
         * @param parkingInfo
         * @return
         */
        private boolean isOut(ParkingInfo parkingInfo) {
            return CacheAdapter.getParkingMap().get(parkingInfo.getCarId()) != null
                    && parkingInfo.getComeTime() == null && parkingInfo.getOutTime() == null;
        }


        private boolean isNewComming(ParkingInfo parkingInfo) {
            return CacheAdapter.getParkingMap().get(parkingInfo.getCarId()) == null
                    && parkingInfo.getComeTime() != null && parkingInfo.getOutTime() == null;
        }


        private boolean isSynchronized(ParkingInfo parkingInfo) {
            return CacheAdapter.getParkingMap().get(parkingInfo.getCarId()) != null
                    && parkingInfo.getComeTime() != null && parkingInfo.getOutTime() == null;
        }

        private boolean notComming(ParkingInfo parkingInfo) {
            return CacheAdapter.getParkingMap().get(parkingInfo.getCarId()) == null
                    && parkingInfo.getComeTime() == null && parkingInfo.getOutTime() == null;
        }


    }

}
