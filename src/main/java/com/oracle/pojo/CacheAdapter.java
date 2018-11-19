package com.oracle.pojo;

import com.oracle.model.Car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheAdapter {

    private static Map<Long, Long> parkingMap = new HashMap<Long, Long>(); //carId-->parkingInfo
    private static List<Car> carList = new ArrayList<>();

    public static Map<Long, Long> getParkingMap() {
        return parkingMap;
    }

    public static void setParkingMap(Map<Long, Long> parkingMap) {
        CacheAdapter.parkingMap = parkingMap;
    }

    public static List<Car> getCarList() {
        return carList;
    }

    public static void setCarList(List<Car> carList) {
        CacheAdapter.carList = carList;
    }
}
