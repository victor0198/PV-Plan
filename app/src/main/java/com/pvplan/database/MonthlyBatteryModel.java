package com.pvplan.database;

public class MonthlyBatteryModel {
    private int projectId;
    private int month;
    private Double productionW_d;
    private Double not_capturedW_d;
    private Double batteryFull;
    private Double batteryEmpty;

    public MonthlyBatteryModel(int projectId, int month, Double productionW_d, Double not_capturedW_d, Double batteryFull, Double batteryEmpty) {
        this.projectId = projectId;
        this.month = month;
        this.productionW_d = productionW_d;
        this.not_capturedW_d = not_capturedW_d;
        this.batteryFull = batteryFull;
        this.batteryEmpty = batteryEmpty;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Double getProductionW_d() {
        return productionW_d;
    }

    public void setProductionW_d(Double productionW_d) {
        this.productionW_d = productionW_d;
    }

    public Double getNot_capturedW_d() {
        return not_capturedW_d;
    }

    public void setNot_capturedW_d(Double not_capturedW_d) {
        this.not_capturedW_d = not_capturedW_d;
    }

    public Double getBatteryFull() {
        return batteryFull;
    }

    public void setBatteryFull(Double batteryFull) {
        this.batteryFull = batteryFull;
    }

    public Double getBatteryEmpty() {
        return batteryEmpty;
    }

    public void setBatteryEmpty(Double batteryEmpty) {
        this.batteryEmpty = batteryEmpty;
    }
}
