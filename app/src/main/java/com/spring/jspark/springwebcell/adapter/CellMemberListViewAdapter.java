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

/**
 * Created by jspark on 2017. 3. 6..
 */

public class CellMemberListViewAdapter extends BaseAdapter{

    private int year;
    private int week;

    ArrayList<CellMemberInfo> mMemberList;

    public CellMemberListViewAdapter(ArrayList<CellMemberInfo> memberList, int year, int week){
        setMemberListInfo(memberList);
        this.year = year;
        this.week = week;
    }

    public void setMemberListInfo(ArrayList<CellMemberInfo> memberList){
        mMemberList = memberList;
    }

    @Override
    public int getCount() {
        return mMemberList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMemberList.get(position);
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
            convertView = inflater.inflate(R.layout.cell_member_list_view_item, parent, false);
        }

        CheckBox checkBox1 = (CheckBox) convertView.findViewById(R.id.checkbox1);
        CheckBox checkBox2 = (CheckBox) convertView.findViewById(R.id.checkbox2);
        TextView textView = (TextView) convertView.findViewById(R.id.textview);
        EditText editText = (EditText) convertView.findViewById(R.id.edittext);

        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("test", "onCheckedChanged position=" + pos + " isWorshipAttended=" + isChecked);
                mMemberList.get(pos).getAttendanceData(year,week).setWorshipAttended(isChecked);
            }
        });

        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("test", "onCheckedChanged position=" + pos + " isCellAttended=" + isChecked);
                mMemberList.get(pos).getAttendanceData(year,week).setCellAttended(isChecked);
            }
        });


        CellMemberInfo info = mMemberList.get(position);

        checkBox1.setChecked( info.getAttendanceData(year,week).isWorshipAttended() );
        checkBox2.setChecked( info.getAttendanceData(year,week).isCellAttended() );
        textView.setText( info.getName() );
        editText.setText( info.getAttendanceData(year,week).getCellAbsentReason() );

        return convertView;
    }
}