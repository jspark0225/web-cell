package com.spring.jspark.springwebcell.httpparser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

        if(!attendanceData.containsKey(key))
            attendanceData.put(key, new AttendanceData());

        return attendanceData.get(key);
    }

    public class AttendanceData{
        private int index;
        private String worshipAbsentReason;
        private String cellAbsentReason;
        private boolean isWorshipAttended = false;
        private boolean isCellAttended = false;

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
}
