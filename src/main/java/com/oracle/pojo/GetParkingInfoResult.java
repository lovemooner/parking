package com.oracle.pojo;

import com.oracle.model.ParkingInfo;

public class GetParkingInfoResult {

    private String type;
    private String tip;
    private Data data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public ParkingInfo toParkingInfo(){
        ParkingInfo parkingInfo=new ParkingInfo();
        Data.OrderConfirmInfo order= getData().orderConfirmInfo;
        parkingInfo.setLotName(order.getLotName());
        parkingInfo.setComeTime(order.getComeTime());
        parkingInfo.setOutTime(order.getOutTime());
        parkingInfo.setCarImg(order.getCarImg());
        return parkingInfo;
    }

    class Data{
        public OrderConfirmInfo orderConfirmInfo;
        private String paySign;

        class OrderConfirmInfo{
             private String carPlateNum;
             private Long lotId;
             private String userId;
             private String lotName;
             private String carImg;
             private String comeTime;
             private String outTime;
             private String ORDER_CATEGORY_COMMON;
             private String paymentMethod;
             private String parkingOrderExpireTime;
             private String elapsedTime;
             private boolean isPay;

            public String getCarPlateNum() {
                return carPlateNum;
            }

            public void setCarPlateNum(String carPlateNum) {
                this.carPlateNum = carPlateNum;
            }

            public Long getLotId() {
                return lotId;
            }

            public void setLotId(Long lotId) {
                this.lotId = lotId;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getLotName() {
                return lotName;
            }

            public void setLotName(String lotName) {
                this.lotName = lotName;
            }

            public String getCarImg() {
                return carImg;
            }

            public void setCarImg(String carImg) {
                this.carImg = carImg;
            }

            public String getComeTime() {
                return comeTime;
            }

            public void setComeTime(String comeTime) {
                this.comeTime = comeTime;
            }

            public String getOutTime() {
                return outTime;
            }

            public void setOutTime(String outTime) {
                this.outTime = outTime;
            }

            public String getORDER_CATEGORY_COMMON() {
                return ORDER_CATEGORY_COMMON;
            }

            public void setORDER_CATEGORY_COMMON(String ORDER_CATEGORY_COMMON) {
                this.ORDER_CATEGORY_COMMON = ORDER_CATEGORY_COMMON;
            }

            public String getPaymentMethod() {
                return paymentMethod;
            }

            public void setPaymentMethod(String paymentMethod) {
                this.paymentMethod = paymentMethod;
            }

            public String getParkingOrderExpireTime() {
                return parkingOrderExpireTime;
            }

            public void setParkingOrderExpireTime(String parkingOrderExpireTime) {
                this.parkingOrderExpireTime = parkingOrderExpireTime;
            }

            public String getElapsedTime() {
                return elapsedTime;
            }

            public void setElapsedTime(String elapsedTime) {
                this.elapsedTime = elapsedTime;
            }

            public boolean isPay() {
                return isPay;
            }

            public void setPay(boolean pay) {
                isPay = pay;
            }
        }
    }
}
