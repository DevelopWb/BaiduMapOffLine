package com.downofflinemap.broadcastReceiver;

/**
 * Author:wang_sir
 * Time:2018/6/1 13:58
 * Description:This is ConnectionChangeReceiver
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.downofflinemap.interfaces.NetStatusCallBack;

/**
 * 监听网络状态广播
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    private NetStatusCallBack netStatusCallBack;

    public ConnectionChangeReceiver(NetStatusCallBack netStatusCallBack) {
        this.netStatusCallBack = netStatusCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!wifiNetInfo.isConnected()) {//网络断开时，将所有任务暂停
            if (netStatusCallBack != null) {
                netStatusCallBack.networkShutDown();
            }

        } else {//网络恢复后，开始所有未完成任务
            if (netStatusCallBack != null) {
                netStatusCallBack.networkRecovery();
            }

        }
    }
}