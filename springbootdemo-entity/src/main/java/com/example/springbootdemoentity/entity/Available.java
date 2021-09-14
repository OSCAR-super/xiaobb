package com.example.springbootdemoentity.entity;

public class Available {
    public String getTodayStart() {
        return todayStart;
    }

    public void setTodayStart(String todayStart) {
        this.todayStart = todayStart;
    }

    public String getTodayEnd() {
        return todayEnd;
    }

    public void setTodayEnd(String todayEnd) {
        this.todayEnd = todayEnd;
    }

    @Override
    public String toString() {
        return "Available{" +
                "todayStart='" + todayStart + '\'' +
                ", todayEnd='" + todayEnd + '\'' +
                '}';
    }

    private String todayStart;
    private String todayEnd;

}
