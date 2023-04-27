package com.pvplan.database;

public class ConsumptionModel {
    private Integer projectId;
    private Double value;
    private String energyUnit;
    private String timeUnit;
    private Integer month;
    private Integer profileId;

    public ConsumptionModel(Integer projectId, Double value, String energyUnit, String timeUnit, Integer month, Integer profileId) {
        this.projectId = projectId;
        this.value = value;
        this.energyUnit = energyUnit;
        this.timeUnit = timeUnit;
        this.month = month;
        this.profileId = profileId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getEnergyUnit() {
        return energyUnit;
    }

    public void setEnergyUnit(String energyUnit) {
        this.energyUnit = energyUnit;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }
}
