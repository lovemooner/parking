package com.oracle.pojo;

import java.util.List;

public class GetCurrentParkingResult {

    private int count;
    private List<CurrentParkingInfo> items;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CurrentParkingInfo> getItems() {
        return items;
    }

    public void setItems(List<CurrentParkingInfo> items) {
        this.items = items;
    }

    public class CurrentParkingInfo {
       private Long id;
        private String car_no;
        private Long car_id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCarNo() {
            return car_no;
        }

        public void setCarNo(String carNo) {
            this.car_no = carNo;
        }

        public Long getCarId() {
            return car_id;
        }

        public void setCarId(Long carId) {
            this.car_id = carId;
        }
    }
}
