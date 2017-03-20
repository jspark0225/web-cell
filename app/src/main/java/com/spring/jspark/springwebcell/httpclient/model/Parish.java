package com.spring.jspark.springwebcell.httpclient.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jspark on 2017. 3. 17..
 */

public class Parish {
    String mParishName;

    HashMap<String, Cell> mCellList = new HashMap<>();

    HashMap<String, Integer> mTotalNumber = new HashMap<>();

    public Parish(String parishName){
        mParishName = parishName;
    }

    public Cell getCell(String leaderName){
        if(!mCellList.containsKey(leaderName))
            mCellList.put(leaderName, new Cell(leaderName, mParishName));

        return mCellList.get(leaderName);
    }

    public void addCell(Cell cell){
        if(mCellList.containsKey(cell.getLeaderName()))
            mCellList.remove(cell.getLeaderName());

        mCellList.put(cell.getLeaderName(), cell);
    }

    public int getNumberOfWorshipAttended(int year, int week){
        int n = 0;

        for(String key : mCellList.keySet()){
            n += mCellList.get(key).getNumberOfWorshipAttendance(year, week);
        }

        return n;
    }

    public int getNumberOfCellAttended(int year, int week){
        int n = 0;

        for(String key : mCellList.keySet()){
            n += mCellList.get(key).getNumberOfCellAttendance(year, week);
        }

        return n;
    }

    public int getNumberOfWorshipAbsence(int year, int week){
        int n = 0;

        for(String key : mCellList.keySet()){
            n += mCellList.get(key).getNumberOfWorshipAbsence(year, week);
        }

        return n;
    }

    public int getNumberOfCellAbsence(int year, int week){
        int n = 0;

        for(String key : mCellList.keySet()){
            n += mCellList.get(key).getNumberOfCellAbsence(year, week);
        }

        return n;
    }

    public int getNumberOfMembers(){
        int n = 0;
        for(String key : mCellList.keySet()){
            n += mCellList.get(key).size();
        }

        return n;
    }

    public HashMap<String, Cell> getCellList(){
        return mCellList;
    }

    public int getTotalNumberOfParish(int year, int week){
        String key = year + "/" + week;
        return mTotalNumber.get(key);
    }

    public void setTotalNumberOfParish(int year, int week, int total){
        String key = year + "/" + week;
        if(mTotalNumber.containsKey(key))
            mTotalNumber.remove(key);

        mTotalNumber.put(key, total);
    }
}