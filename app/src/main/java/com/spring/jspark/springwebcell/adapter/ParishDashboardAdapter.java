package com.spring.jspark.springwebcell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.httpparser.CellMemberInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jspark on 2017. 3. 6..
 */

public class ParishDashboardAdapter extends BaseAdapter{

    private ArrayList<ViewData> viewDataList = new ArrayList<>();

    public ParishDashboardAdapter(){
    }

    public int getTotal(){
        int total = 0;
        for(ViewData v : viewDataList)
            total += v.nTotal;

        return total;
    }

    public int getWorshipAttendance(){
        int worshipAttendance = 0;
        for(ViewData v : viewDataList)
            worshipAttendance += v.nWorshipAttendance;

        return worshipAttendance;
    }

    public int getCellAttendance(){
        int cellAttendance = 0;
        for(ViewData v : viewDataList)
            cellAttendance += v.nCellAttendance;

        return cellAttendance;
    }


    public void addData(String leaderName, int nTotal, int nCellAttendance, int nWorshipAttendance){
        boolean isAlreadyExist = false;
        for(ViewData d : viewDataList){
            if(d.leaderName.equals(leaderName)){
                isAlreadyExist = true;
                d.nTotal = nTotal;
                d.nCellAttendance = nCellAttendance;
                d.nWorshipAttendance = nWorshipAttendance;
            }
        }

        if(!isAlreadyExist){
            ViewData viewData = new ViewData();
            viewData.leaderName = leaderName;
            viewData.nCellAttendance = nCellAttendance;
            viewData.nWorshipAttendance = nWorshipAttendance;

            viewDataList.add(viewData);
        }
    }

    @Override
    public int getCount() {
        return viewDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return viewDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parish_dashboard_list_view_item, parent, false);
        }

        TextView leaderNameView = (TextView) convertView.findViewById(R.id.leader_name);
        TextView attendanceInfoView = (TextView) convertView.findViewById(R.id.attendance_info);

        leaderNameView.setText( viewDataList.get(pos).leaderName );
        attendanceInfoView.setText( "주일예배(" + viewDataList.get(pos).nWorshipAttendance + "/" + viewDataList.get(pos).nTotal +"), "
                                +"셀모임(" + viewDataList.get(pos).nCellAttendance  + "/" + viewDataList.get(pos).nTotal +")");

        return convertView;
    }

    class ViewData{
        public String leaderName;
        public int nTotal;
        public int nCellAttendance;
        public int nWorshipAttendance;
    }
}