package com.downofflinemap;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Author:wang_sir
 * Time:2018/5/31 15:58
 * Description:This is MyApplication
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }
}
