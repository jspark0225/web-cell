package com.spring.jspark.springwebcell.httpparser;

import android.os.Parcel;
import android.os.Parcelable;

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
    private boolean isChecked;

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getEncodedName(){
        try {
            String encodedName =  URLEncoder.encode(name, "euc-kr");
            return encodedName;
        } catch (UnsupportedEncodingException e) {
            return name;
        }
    }

    public String getEncodedReason(){
        try {
            String encodedName =  URLEncoder.encode(reason, "euc-kr");
            return encodedName;
        } catch (UnsupportedEncodingException e) {
            return reason;
        }
    }
}
