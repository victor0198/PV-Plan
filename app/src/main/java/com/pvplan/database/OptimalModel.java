package com.pvplan.database;

public class OptimalModel {
    private int id;
    private String P_power;
    private String S_power;
    private String slope;
    private String azimuth;

    public OptimalModel(int id, String p_power, String s_power, String slope, String azimuth) {
        this.id = id;
        P_power = p_power;
        S_power = s_power;
        this.slope = slope;
        this.azimuth = azimuth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getP_power() {
        return P_power;
    }

    public void setP_power(String p_power) {
        P_power = p_power;
    }

    public String getS_power() {
        return S_power;
    }

    public void setS_power(String s_power) {
        S_power = s_power;
    }

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
