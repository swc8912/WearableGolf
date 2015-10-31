package com.kingofgolf.golfapp;

/**
 * Created by 우철 on 2015-10-31.
 */
public class SensorData {
    private String sensorType;
    private double arg1;
    private double arg2;
    private double arg3;

    public SensorData() {

    }

    public SensorData(String type, double f1, double f2, double f3) {
        sensorType = type;
        arg1 = f1;
        arg2 = f2;
        arg3 = f3;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public double getArg1() {
        return arg1;
    }

    public void setArg1(double arg1) {
        this.arg1 = arg1;
    }

    public double getArg2() {
        return arg2;
    }

    public void setArg2(double arg2) {
        this.arg2 = arg2;
    }

    public double getArg3() {
        return arg3;
    }

    public void setArg3(double arg3) {
        this.arg3 = arg3;
    }
}
