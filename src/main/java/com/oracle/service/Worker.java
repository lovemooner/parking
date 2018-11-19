package com.oracle.service;

import com.oracle.model.Car;
import com.oracle.model.ParkingInfo;
import com.oracle.pojo.CacheAdapter;
import com.oracle.pojo.Constants;
import love.moon.common.HttpResponse;
import love.moon.util.DateUtil;
import love.moon.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Worker implements IWorker {
    public static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    private ParkingService service = new ParkingService();


    @Override
    public void doWork() {
        List<Car> carList = CacheAdapter.getCarList();
        while (true) {
            for (Car car : carList) {
                ParkingInfo parkingInfo = service.requestParkingInfo(car.getCarNo());
                if(parkingInfo==null){
                    LOG.error("error info car_id:{}",car.getId());
                    continue;
                }
                parkingInfo.setCarId(car.getId());
                parkingInfo.setId(CacheAdapter.getParkingMap().get(car.getId()));
                if (notComming(parkingInfo)) {
                    LOG.info("Car:" + parkingInfo.getCarId() + " is not comming.");
                    continue;
                }
                if (isSynchronized(parkingInfo)) {
                    LOG.info("Car:" + parkingInfo.getCarId() + " is  synchronized.");
                    continue;
                }
                if (isNewComming(parkingInfo)) {
                    HttpResponse response = service.postParkingInfo(parkingInfo);
                    if (response.getCode() == 200) {
                        CacheAdapter.getParkingMap().put(parkingInfo.getCarId(), NumberUtil.longValue(response.getContent()));
                    }
                } else if (isOut(parkingInfo)) {
                    parkingInfo.setOutTime(DateUtil.getChinaTime());
                    HttpResponse response = service.postParkingInfo(parkingInfo);
                    if (response.getCode() == 200) {
                        CacheAdapter.getParkingMap().remove(car.getId());
                    }
                }

            }
            try {
                LOG.info("Worker will sleep {} ms",Constants.FETCH_SLEEP_TIME);
                Thread.sleep(Constants.FETCH_SLEEP_TIME);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
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
