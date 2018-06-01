package com.downofflinemap.interfaces;

/**
 * Author:wang_sir
 * Time:2018/6/1 13:59
 * Description:This is NetStatusCallBack
 */
public interface NetStatusCallBack {

    void networkRecovery();//网络状态恢复
    void networkShutDown();//网络断开
}
