package com.spring.jspark.springwebcell.httpclient.model;

import java.util.ArrayList;

/**
 * Created by jspark on 2017. 3. 17..
 */

public class CellInfo {
    ArrayList<CellMemberInfo> mCellInfo = new ArrayList<>();

    String mCellLeaderName;
    String parish;


    public CellInfo(String cellLeaderName, String parish){
        mCellLeaderName = cellLeaderName;
        parish = parish;
    }
}
