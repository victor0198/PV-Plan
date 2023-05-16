package com.pvplan.database;

public class ProjectModel {
    private int id;
    private String name;
    private Double power;
    private Double battery;
    private String latitude;
    private String longitude;
    private String slope;
    private String azimuth;

    public ProjectModel(int id, String name, Double power, Double battery, String latitude, String longitude, String slope, String azimuth) {
        this.id = id;
        this.name = name;
        this.power = power;
        this.battery = battery;
        this.latitude = latitude;
        this.longitude = longitude;
        this.slope = slope;
        this.azimuth = azimuth;
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", power=" + power +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPower() {
        return power;
    }

    public Double getBattery() {
        return battery;
    }

    public void setBattery(Double battery) {
        this.battery = battery;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {this.longitude = longitude; }

    public String getSlope() {
        return slope;
    }

    public void setSlope(String slope) {
        this.slope = slope;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }
}