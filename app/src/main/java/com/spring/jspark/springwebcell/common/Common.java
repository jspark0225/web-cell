package com.spring.jspark.springwebcell.common;

import com.spring.jspark.springwebcell.httpclient.model.Attendance;
import com.spring.jspark.springwebcell.httpclient.model.CellMember;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jspark on 2017. 3. 6..
 */

public class Common {
    public static final String REASON_DELIMETER = "///";

    public static final String[] SPRING_PARISHS = {"청년사역", "1교구", "2교구", "3교구", "4교구", "5교구", "6교구", "7교구"};

    public static int getTodaysWeekOfYear(){
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }

    public static int getTodaysYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    public static int getTodaysMonth() { return Calendar.getInstance().get(Calendar.MONTH); }
    public static int getMonth(int year, int week){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);;
        cal.set(Calendar.WEEK_OF_YEAR, week);

        return cal.get(Calendar.MONTH);
    }

    public static int getDeafaultDate(){
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 1;
    }

    public static int getDefaultDate(int year, int week){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);;
        cal.set(Calendar.WEEK_OF_YEAR, week);

        return cal.get(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_WEEK) + 1;
    }

    public static boolean isPastor(String name, String parish){
        if(name.equals("윤종인") && parish.equals("1교구"))
            return true;
        else if(name.equals("양재훈") && parish.equals("2교구"))
            return true;
        else if(name.equals("이상억") && parish.equals("3교구"))
            return true;
        else if(name.equals("김지훈") && parish.equals("4교구"))
            return true;
        else if(name.equals("임효완") && parish.equals("5교구"))
            return true;
        else if(name.equals("두성광") && parish.equals("6교구"))
            return true;
        else if(name.equals("배성혜") && parish.equals("7교구"))
            return true;
        else
            return false;
    }
}
