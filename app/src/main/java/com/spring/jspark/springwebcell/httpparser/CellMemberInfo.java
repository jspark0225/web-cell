package com.spring.jspark.springwebcell.httpparser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jspark on 2017. 3. 6..
 */

public class CellMemberInfo{
    private int index;
    private String name;
    private String id;
    private String reason;
    private boolean isWorshipAttended;
    private boolean isCellAttended;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
