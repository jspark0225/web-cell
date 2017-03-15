package com.spring.jspark.springwebcell.utils;

import android.content.Context;

/**
 * Created by jspark on 2017. 3. 15..
 */

public class ResourceManager {
    private static ResourceManager mInstance = null;

    private Context context;

    private ResourceManager(){}

    public static ResourceManager getInstance(){
        if(mInstance == null)
            mInstance = new ResourceManager();

        return mInstance;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public String getString(int id){
        return context.getResources().getString(id);
    }
}
