package com.spring.jspark.springwebcell.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jspark on 2017. 3. 15..
 */

public class SharedPreferenceManager {
    public static final String ID = "id";
    public static final String PASSWORD = "pw";
    public static final String PARISH = "parishPosition";
    public static final String LOGIN_ENABLED = "login_enabled";


    private static SharedPreferenceManager mInstance;

    Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private SharedPreferenceManager(){

    }

    public static SharedPreferenceManager getInstance(){
        if(mInstance == null)
            mInstance = new SharedPreferenceManager();

        return mInstance;
    }

    public void setContex(Context context){
        this.context = context;
        pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void putLoginData(String id, String password, int parish, boolean isLogin){
        if(context == null)
            return;

        editor.putString(ID, id);
        editor.putString(PASSWORD, password);
        editor.putInt(PARISH, parish);
        editor.putBoolean(LOGIN_ENABLED, isLogin);
        editor.commit();
    }

    public String getStoredId(){
        if(context == null)
            return "";

        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        return pref.getString(ID, "");
    }

    public String getStoredPassword(){
        if(context == null)
            return "";

        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        return pref.getString(PASSWORD, "");
    }

    public int getStoredParish(){
        if(context == null)
            return 0;

        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        return pref.getInt(PARISH, 0);
    }

    public boolean getStoredLoginEnabled(){
        if(context == null)
            return false;

        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        return pref.getBoolean(LOGIN_ENABLED, false);
    }

    public void clearLoginData(){
        editor.remove(ID);
        editor.remove(PASSWORD);
        editor.remove(PARISH);
        editor.remove(LOGIN_ENABLED);
        editor.apply();
    }
}