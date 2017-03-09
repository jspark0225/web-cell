package com.spring.jspark.springwebcell.httpclient.model;

import java.util.HashMap;

/**
 * Created by jspark on 2017. 3. 6..
 */

public class CellMemberInfo{
    private String name;
    private String id;
    private String phoneNumber;
    private String registeredDate;
    private String birthday;
    private String address;

    // key = year/week
    private HashMap<String, AttendanceData> attendanceData = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AttendanceData getAttendanceData(int year, int week) {
        String key = year + "/" + week;

        if(!attendanceData.containsKey(key)){
            attendanceData.put(key, new AttendanceData(year, week));
        }

        return attendanceData.get(key);
    }
}
