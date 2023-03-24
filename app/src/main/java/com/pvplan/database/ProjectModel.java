package com.pvplan.database;

public class ProjectModel {
    private int id;
    private String name;
    private int power;

    public ProjectModel(int id, String name, int power) {
        this.id = id;
        this.name = name;
        this.power = power;
    }

    @Override
    public String toString() {
        return "CustomerModel{" +
                "credentialId=" + id +
                ", service='" + name + '\'' +
                ", login='" + power + '\'' +
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

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}