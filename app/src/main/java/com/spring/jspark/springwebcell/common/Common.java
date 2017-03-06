package com.spring.jspark.springwebcell.common;

import java.util.Calendar;

/**
 * Created by jspark on 2017. 3. 6..
 */

public class Common {

    public static final String[] SPRING_PARISHS = {"청년사역", "1교구", "2교구", "3교구", "4교구", "5교구", "6교구", "7교구"};

    public static int getTodaysWeekOfYear(){
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }

    public static int getTodaysYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}
