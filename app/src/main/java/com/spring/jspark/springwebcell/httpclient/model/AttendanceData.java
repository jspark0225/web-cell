package com.spring.jspark.springwebcell.httpclient.model;

/**
 * Created by jspark on 2017. 3. 9..
 */

public class AttendanceData {
    private int index;
    private String worshipAbsentReason;
    private String cellAbsentReason;
    private boolean isWorshipAttended = false;
    private boolean isCellAttended = false;
    private int year;
    private int week;

    public AttendanceData(int year, int week){
        this.year = year;
        this.week = week;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getWorshipAbsentReason() {
        return worshipAbsentReason;
    }

    public void setWorshipAbsentReason(String worshipAbsentReason) {
        this.worshipAbsentReason = worshipAbsentReason;
    }

    public String getCellAbsentReason() {
        return cellAbsentReason;
    }

    public void setCellAbsentReason(String cellAbsentReason) {
        this.cellAbsentReason = cellAbsentReason;
    }

    public boolean isWorshipAttended() {
        return isWorshipAttended;
    }

    public void setWorshipAttended(boolean worshipAttended) {
        isWorshipAttended = worshipAttended;
    }

    public boolean isCellAttended() {
        return isCellAttended;
    }

    public void setCellAttended(boolean cellAttended) {
        isCellAttended = cellAttended;
    }
}
