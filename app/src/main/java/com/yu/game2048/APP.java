package com.yu.game2048;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * CREATED BY DY ON 2019/8/23.
 * TIME BY 16:54.
 **/
public class APP extends Application {
    private static APP context;
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        context = this;
    }
    public static APP getContext() {
        return context;
    }
}
