package com.oracle.service;

import com.oracle.model.Car;
import com.oracle.model.ParkingInfo;
import com.oracle.util.CacheAdapter;
import love.moon.common.HttpResponse;
import love.moon.util.DateUtil;
import love.moon.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker implements Runnable {
    public static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    private ParkingService service = new ParkingService();
    private Car car;


    public Worker(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
            ParkingInfo parkingInfo = service.requestParkingInfo(car.getCarNo());
            if (parkingInfo == null) {
                LOG.error("error info car_id:{}", car.getId());
                return;
            }
            parkingInfo.setCarId(car.getId());
            parkingInfo.setId(CacheAdapter.getParkingMap().get(car.getId()));
            if (notComming(parkingInfo)) {
                LOG.debug("Car:" + parkingInfo.getCarId() + ",employee is  not comming or does not driving car.");
                return;
            }
            if (isSynchronized(parkingInfo)) {
                LOG.debug("Car:" + parkingInfo.getCarId() + " is  synchronized.");
                return;
            }
            if (isNewComming(parkingInfo)) {
                HttpResponse response = service.postParkingInfo(parkingInfo);
                if (response.getCode() == 200) {
                    CacheAdapter.getParkingMap().put(parkingInfo.getCarId(), NumberUtil.longValue(response.getContent()));
                    LOG.info("Car is Coming,carId:{}", car.getId());
                }
            } else if (isOut(parkingInfo)) {
                parkingInfo.setOutTime(DateUtil.getChinaTime());
                HttpResponse response = service.putParkingInfo(parkingInfo);
                if (response.getCode() == 200) {
                    CacheAdapter.getParkingMap().remove(car.getId());
                    LOG.info("Car is Out,carId:{}", car.getId());
                }
            }
    }


    private boolean isSynchronized(ParkingInfo parkingInfo) {
        return CacheAdapter.getParkingMap().get(parkingInfo.getCarId()) != null
                && parkingInfo.getComeTime() != null && parkingInfo.getOutTime() == null;
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

    private boolean notComming(ParkingInfo parkingInfo) {
        return CacheAdapter.getParkingMap().get(parkingInfo.getCarId()) == null
                && parkingInfo.getComeTime() == null && parkingInfo.getOutTime() == null;
    }

    private boolean isNewComming(ParkingInfo parkingInfo) {
        return CacheAdapter.getParkingMap().get(parkingInfo.getCarId()) == null
                && parkingInfo.getComeTime() != null && parkingInfo.getOutTime() == null;
    }


}
