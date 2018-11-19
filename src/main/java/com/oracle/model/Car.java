package com.oracle.model;

public class Car {
    private Long id;
    private String owner;
    private String car_no;
    private String mobile;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCarNo() {
        return car_no;
    }

    public void setCarNo(String carNo) {
        this.car_no = carNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
