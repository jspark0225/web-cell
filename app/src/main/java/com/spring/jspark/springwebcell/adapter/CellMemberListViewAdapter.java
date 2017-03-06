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
    ArrayList<CellMemberInfo> mMemberList;

    public CellMemberListViewAdapter(ArrayList<CellMemberInfo> memberList){
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

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
        TextView textView = (TextView) convertView.findViewById(R.id.textview);
        EditText editText = (EditText) convertView.findViewById(R.id.edittext);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("test", "onCheckedChanged position=" + pos + " isChecked=" + isChecked);
                mMemberList.get(pos).setChecked(isChecked);
            }
        });

        CellMemberInfo info = mMemberList.get(position);

        checkBox.setChecked( info.isChecked() );
        textView.setText( info.getName() );
        editText.setText( info.getReason() );

        return convertView;
    }
}
