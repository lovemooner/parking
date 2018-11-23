package com.oracle.util;

import com.oracle.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheAdapter {

    private static Map<Long, Long> parkingMap = new ConcurrentHashMap<Long, Long>(); //carId-->parkingInfo
    private static List<Car> carList = new ArrayList<>();

    public static Map<Long, Long> getParkingMap() {
        return parkingMap;
    }

    public static void setParkingMap(Map<Long, Long> parkingMap) {
        CacheAdapter.parkingMap = parkingMap;
    }

    public static List<Car> get() {
        return carList;
    }

    public static void set(List<Car> carList) {
        CacheAdapter.carList = carList;
    }
}
