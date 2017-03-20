package com.spring.jspark.springwebcell.httpclient.model;

import com.spring.jspark.springwebcell.common.Common;

import java.util.ArrayList;

/**
 * Created by jspark on 2017. 3. 17..
 */

public class Cell {
    ArrayList<CellMember> mCellInfo = new ArrayList<>();

    String mCellLeaderName;
    String mParish;


    public Cell(String cellLeaderName, String parish){
        mCellLeaderName = cellLeaderName;
        this.mParish = parish;
    }

    public ArrayList<CellMember> getCellMemberList(){
        return mCellInfo;
    }

    public String getLeaderName(){
        return mCellLeaderName;
    }

    public CellMember getCellMember(String name){
        CellMember member = getExistCellMember(name);

        if(member == null){
            member = new CellMember();
            member.setName(name);
            mCellInfo.add(member);
        }

        return member;
    }

    public CellMember getCellMemberById(String id){
        for(CellMember m : mCellInfo){
            if(m.getId().equals(id))
                return m;
        }

        return null;
    }

    public CellMember getCellMemberByPosition(int position){
        if(position >= mCellInfo.size())
            return new CellMember();

        return mCellInfo.get(position);
    }

    public void add(CellMember member){
        CellMember existMember = getExistCellMember(member.getName());
        if(existMember == null){
            mCellInfo.add(member);
        }else{
            mCellInfo.remove(existMember);
            mCellInfo.add(member);
        }
    }

    private CellMember getExistCellMember(String name){
        for(CellMember member : mCellInfo){
            if(member.getName() != null && member.getName().equals(name))
                return member;
        }
        return null;
    }

    public int size(){
        return mCellInfo.size();
    }

    public void setAttendanceData(int index, int year, int week, boolean isWorshipAttended, boolean isCellAttended, String reason){
        if(index >= size() || index < 0)
            return;

        Attendance data = mCellInfo.get(index).getAttendanceData(year, week);
        data.setWorshipAttended(isWorshipAttended);
        data.setCellAttended(isCellAttended);
        data.setAbsentReason(reason);
    }

    public int getNumberOfWorshipAttendance(int year, int week){
        int n = 0;
        for(CellMember member : mCellInfo){
            if(member.getAttendanceData(year, week).isWorshipAttended())
                n++;
        }

        return n;
    }

    public int getNumberOfCellAttendance(int year, int week){
        int n = 0;
        for(CellMember member : mCellInfo){
            if(member.getAttendanceData(year, week).isCellAttended())
                n++;
        }

        return n;
    }

    public int getNumberOfWorshipAbsence(int year, int week){
        int n = 0;
        for(CellMember member : mCellInfo){
            if(!member.getAttendanceData(year, week).isWorshipAttended())
                n++;
        }

        return n;
    }

    public int getNumberOfCellAbsence(int year, int week){
        int n = 0;
        for(CellMember member : mCellInfo){
            if(!member.getAttendanceData(year, week).isCellAttended())
                n++;
        }

        return n;
    }

}
