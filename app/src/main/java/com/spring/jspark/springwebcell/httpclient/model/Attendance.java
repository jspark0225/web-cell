package com.spring.jspark.springwebcell.httpclient.model;

/**
 * Created by jspark on 2017. 3. 9..
 */

public class Attendance {
    private int index;
    private String absentReason;
    private boolean isWorshipAttended = false;
    private boolean isCellAttended = false;
    private boolean isWorshipChecked = false;
    private boolean isCellChecked = false;
    private int year;
    private int week;

    public Attendance(int year, int week){
        this.year = year;
        this.week = week;
        absentReason = "";
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAbsentReason() {
        return absentReason;
    }

    public void setAbsentReason(String absentReason) {
        this.absentReason = absentReason;
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

    public boolean isAbsentReasonExists(){
        return (absentReason != null && !absentReason.isEmpty());
    }

    public boolean isWorshipChecked() {
        return isWorshipChecked;
    }

    public void setWorshipChecked(boolean worshipChecked) {
        isWorshipChecked = worshipChecked;
    }

    public boolean isCellChecked() {
        return isCellChecked;
    }

    public void setCellChecked(boolean cellChecked) {
        isCellChecked = cellChecked;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "index=" + index +
                ", absentReason='" + absentReason + '\'' +
                ", isWorshipAttended=" + isWorshipAttended +
                ", isCellAttended=" + isCellAttended +
                ", year=" + year +
                ", week=" + week +
                '}';
    }
}
