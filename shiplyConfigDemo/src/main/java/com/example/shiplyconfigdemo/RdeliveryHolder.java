package com.example.shiplyconfigdemo;


import com.tencent.rdelivery.RDelivery;

class RdeliveryHolder {

    private static RdeliveryHolder instance = new RdeliveryHolder();
    private RDelivery rDelivery;
    private RdeliveryHolder() {}

    public static RdeliveryHolder getInstance() {
        return instance;
    }

    public void setRdeliveryInstance(RDelivery rd) {
        rDelivery = rd;
    }

    public RDelivery getRdelivery() {
        return rDelivery;
    }
}
