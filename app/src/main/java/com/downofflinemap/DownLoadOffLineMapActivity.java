package com.downofflinemap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.downofflinemap.adapter.CityExpandableAdapter;
import com.downofflinemap.adapter.DownloadedMapsAdapter;
import com.downofflinemap.broadcastReceiver.ConnectionChangeReceiver;
import com.downofflinemap.interfaces.NetStatusCallBack;
import com.downofflinemap.interfaces.UpdateFinishedCallBack;

import java.util.ArrayList;
import java.util.List;


public class DownLoadOffLineMapActivity extends Activity implements OnClickListener,
        MKOfflineMapListener ,UpdateFinishedCallBack,NetStatusCallBack{

    private ImageView download_back;
    private TextView citylist_top_tv1;
    private TextView citylist_top_tv2;
    private ListView downloadedMapsView;
    private LinearLayout citylist_ll;
    private RelativeLayout localmap_layout;
    private MKOfflineMap mOffline = null;
    private ExpandableListView cityExpandable_lv;
    private ArrayList<MKOLUpdateElement> downloadedMaps =new ArrayList<MKOLUpdateElement>();
    private DownloadedMapsAdapter lAdapter = null;
    private LinearLayout description_ll;
    private LinearLayout delete_all_ll;
    private LinearLayout download_all_ll;
    private LinearLayout pause_all_ll;


    private CityExpandableAdapter cityExpandAdapter;
    private ImageView download_all_iv;
    private ImageView pause_all_iv;
    private TextView download_all_tv;
    private TextView pause_all_tv;
    //初始化网络状态广播
    private ConnectionChangeReceiver myReceiver;
    private String Tag = "DownLoadOffLineMapActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.downloadoffpage);
        mOffline = new MKOfflineMap();
        mOffline.init(this);
        findView();
        initView();
        registNetWorkConnectionReceiver();


    }

    /**
     * 注册网络状态变化的广播
     */
    private void registNetWorkConnectionReceiver() {
        //网络状态
        IntentFilter filterz = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new ConnectionChangeReceiver(this);
        this.registerReceiver(myReceiver, filterz);
    }

    @Override
    protected void onResume() {
        updateView();
        cityExpandAdapter.notifyDataSetChanged();
        initBottomButtonBg();
        super.onResume();
    }

    /**
     * 更改底部按钮的bg
     */
    private void initBottomButtonBg() {
        downloadedMaps = mOffline.getAllUpdateInfo();
        if (downloadedMaps==null) {
          downloadedMaps =new ArrayList<MKOLUpdateElement>();
        }
        if (downloadedMaps.size() > 0) {
            ArrayList<MKOLUpdateElement> pauseDates = new ArrayList<MKOLUpdateElement>();
            ArrayList<MKOLUpdateElement> downloadDates = new ArrayList<MKOLUpdateElement>();
            for (MKOLUpdateElement bean : downloadedMaps) {
                if (bean.status == 1 || bean.status == 2) {
                    pauseDates.add(bean);
                } else if (bean.status == 3||bean.status == 8) {
                    downloadDates.add(bean);
                }

            }
            if (pauseDates.size() > 0) {
                pause_all_ll.setBackgroundResource(R.drawable.pause_all_press);
                pause_all_iv.setBackgroundResource(R.drawable.pause_hight);
                pause_all_tv.setTextColor(getResources().getColor(R.color.white));
            } else {
                pause_all_ll.setBackgroundResource(R.drawable.pause_all_normal);
                pause_all_iv.setBackgroundResource(R.drawable.pause_grey);
                pause_all_tv.setTextColor(getResources().getColor(R.color.grey));
            }
            if (downloadDates.size() > 0) {
                download_all_iv
                        .setBackgroundResource(R.drawable.download_hight);
                download_all_tv.setTextColor(getResources().getColor(
                        R.color.white));
                download_all_ll
                        .setBackgroundResource(R.drawable.download_all_bg);
            } else {
                download_all_iv.setBackgroundResource(R.drawable.download_grey);
                download_all_tv.setTextColor(getResources()
                        .getColor(R.color.grey));
                download_all_ll
                        .setBackgroundResource(R.drawable.download_all_bg_grey);
            }
        }

    }

    private void initView() {
        ArrayList<List<MKOLSearchRecord>> child = new ArrayList<List<MKOLSearchRecord>>();

        // 获取所有支持离线地图的城市
        final ArrayList<MKOLSearchRecord> group = mOffline
                .getOfflineCityList();
        for (MKOLSearchRecord bean : group) {

            if (bean.childCities != null) {
                ArrayList<MKOLSearchRecord> list = new ArrayList<MKOLSearchRecord>();
                list.add(bean);
                for (MKOLSearchRecord bean_ : bean.childCities) {
                    list.add(bean_);
                }
                child.add(list);
            } else {
                ArrayList<MKOLSearchRecord> list = new ArrayList<MKOLSearchRecord>();
                child.add(list);
            }

        }
        initCityExpandableListView(group,child);
        initDownloadedMapsView();
        // 获取已下过的离线地图信息
        downloadedMaps = mOffline.getAllUpdateInfo();
        if (downloadedMaps == null) {
            downloadedMaps = new ArrayList<MKOLUpdateElement>();
        }
        if (downloadedMaps.isEmpty()) {
            description_ll.setVisibility(View.VISIBLE);
            citylist_ll.setVisibility(View.GONE);
            localmap_layout.setVisibility(View.GONE);
        } else {
            localmap_layout.setVisibility(View.VISIBLE);
            citylist_ll.setVisibility(View.GONE);
            description_ll.setVisibility(View.GONE);
        }
    }
    /**
     * 初始化CityExpandableListView的item点击事件
     */
    private void initCityExpandableListView(final ArrayList<MKOLSearchRecord> group,final ArrayList<List<MKOLSearchRecord>> child) {
        cityExpandAdapter = new CityExpandableAdapter(this, group, child,
                mOffline);
        cityExpandable_lv.setAdapter(cityExpandAdapter);
        cityExpandable_lv.setOnGroupClickListener(new OnGroupClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public boolean onGroupClick(ExpandableListView arg0, View view,
                                        int arg2, long arg3) {
                MKOLSearchRecord bean = group.get(arg2);
                if (bean.childCities == null) {
                    if (getDownloadingCitys() > 25) {
                        Log.i(Tag, "==-----" + getDownloadingCitys());
                        Toast.makeText(DownLoadOffLineMapActivity.this, "超过最大同时下载数", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if (!getUpdatedCitys().contains(1)) {
                        mOffline.start(1);
                    }
                    int cityid = bean.cityID;
                    if (getUpdatedCitys().contains(cityid)) {
                        Toast.makeText(DownLoadOffLineMapActivity.this, "已添加到下载列表", 0)
                                .show();
                        switchToDownLoad();
                    } else {
                        mOffline.start(cityid);
                        Toast.makeText(DownLoadOffLineMapActivity.this, "已添加到下载任务", 0)
                                .show();
                    }

                    updateView();
                    cityExpandAdapter.notifyDataSetChanged();
                    return true;
                } else {
                    return false;// false 代表group展开
                }

            }
        });
        cityExpandable_lv.setOnChildClickListener(new OnChildClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public boolean onChildClick(ExpandableListView arg0, View view,
                                        int groupPosition, int childPosition, long arg4) {
                MKOLSearchRecord bean = child.get(groupPosition).get(
                        childPosition);
                int cityid = bean.cityID;
                if (!getUpdatedCitys().contains(1)) {
                    mOffline.start(1);
                }
                if (getUpdatedCitys().contains(cityid)) {

                    Toast.makeText(DownLoadOffLineMapActivity.this, "已添加到下载列表", 0)
                            .show();
                    switchToDownLoad();

                } else {
                    if (bean.cityType == 1) {
                        if (getDownloadingCitys() + bean.childCities.size() > 25) {
                            Log.i(Tag, "==-----" + getDownloadingCitys());
                            Toast.makeText(DownLoadOffLineMapActivity.this, "超过最大同时下载数", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    } else {
                        if (getDownloadingCitys() > 25) {
                            Log.i(Tag, "==-----" + getDownloadingCitys());
                            Toast.makeText(DownLoadOffLineMapActivity.this, "超过最大同时下载数", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                    mOffline.start(cityid);
                    if (childPosition == 0) {
                        switchToDownLoad();
                    }
                    Toast.makeText(DownLoadOffLineMapActivity.this, "已添加到下载任务", 0)
                            .show();
                }
                updateView();
                cityExpandAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    /**
     * 初始化下载城市列表的listView和item点击事件
     */
    private void initDownloadedMapsView() {
        lAdapter = new DownloadedMapsAdapter(this,mOffline.getAllUpdateInfo(),this);
        downloadedMapsView.setAdapter(lAdapter);
        downloadedMapsView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                MKOLUpdateElement bean = downloadedMaps.get(arg2);
                final int cityid = bean.cityID;
                TextView tv = (TextView) view.findViewById(R.id.citydelete_tv);

                if (bean.status == 1) {// 正在下载
                    mOffline.pause(cityid);
                    tv.setText("已暂停");

                } else if (bean.status == 2) {// 等待下载
                    mOffline.pause(cityid);
                    tv.setText("已暂停");
                } else if (bean.status == 3) {// 已暂停
                    mOffline.start(cityid);
                    tv.setText("暂停下载");
                }
                updateView();
                initBottomButtonBg();
            }
        });
        downloadedMapsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MKOLUpdateElement bean = downloadedMaps.get(position);
                final int cityid = bean.cityID;
                deleteOfflineMapDialog(cityid);
                return false;
            }
        });
    }

    /**
     * 获取所有已下过的离线地图的城市ID
     * @return
     */
    public ArrayList<Integer> getUpdatedCitys() {
        ArrayList<Integer> arrays = new ArrayList<Integer>();
        // 获取已下过的离线地图信息
        downloadedMaps = mOffline.getAllUpdateInfo();
        if (downloadedMaps==null) {
            downloadedMaps =new ArrayList<MKOLUpdateElement>();
        }
        if (downloadedMaps.size() != 0) {
            for (MKOLUpdateElement bean_ : downloadedMaps) {
                arrays.add(bean_.cityID);
            }
        }
        return arrays;
    }

    /**
     * 删除离线地图的对话框
     * @param cityid
     */
    private void deleteOfflineMapDialog(final int cityid) {
        View v = LayoutInflater.from(this).inflate(R.layout.back_dialog, null);
        final Dialog dialog_c = new Dialog(this, R.style.DialogStyle);
        dialog_c.setCanceledOnTouchOutside(true);
        dialog_c.show();
        Window window = dialog_c.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        lp.width = dip2px(this, 300); // 宽度
        lp.height = dip2px(this, 230); // 高度
        // lp.alpha = 0.7f; // 透明度
        window.setAttributes(lp);
        window.setContentView(v);
        TextView confirm_tv = (TextView) v.findViewById(R.id.confirm_tv);
        TextView cancel_tv = (TextView) v.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_c.dismiss();
            }
        });
        confirm_tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (cityid == -1) {
                    downloadedMaps = mOffline.getAllUpdateInfo();
                    if (downloadedMaps == null) {
                        downloadedMaps = new ArrayList<MKOLUpdateElement>();
                    }
                    if (!downloadedMaps.isEmpty()) {
                        for (MKOLUpdateElement bean : downloadedMaps) {
                            mOffline.remove(bean.cityID);
                        }
                    }
                } else {
                    mOffline.remove(cityid);
                }
                dialog_c.dismiss();
                updateView();
                initBottomButtonBg();
                downloadedMaps = mOffline.getAllUpdateInfo();
                if (downloadedMaps==null||downloadedMaps.isEmpty()) {
                    description_ll.setVisibility(View.VISIBLE);
                    citylist_ll.setVisibility(View.GONE);
                    localmap_layout.setVisibility(View.GONE);
                }

            }
        });

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onPause() {
        pauseAllUnDownLoadCity();

        super.onPause();
    }

    /**
     * 将所有未下载完成的城市暂停
     */
    private void pauseAllUnDownLoadCity() {
        ArrayList<MKOLUpdateElement> downloadedMaps = mOffline.getAllUpdateInfo();
        if (downloadedMaps != null && !downloadedMaps.isEmpty()) {
            for (MKOLUpdateElement bean : downloadedMaps) {
                if (bean.status == 1 || bean.status == 2) {
                    mOffline.pause(bean.cityID);

                }
            }
        }
        updateView();
        initBottomButtonBg();
    }

    @Override
    protected void onDestroy() {
        /**
         * 退出时，销毁离线地图模块
         */
        mOffline.destroy();
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }


    /**
     * 开始下载所有的未下载完成的城市
     */
    private void startDownLoadAllCitys() {
        downloadedMaps = mOffline.getAllUpdateInfo();
        if (downloadedMaps==null) {
            downloadedMaps =new ArrayList<MKOLUpdateElement>();
        }
        if (!downloadedMaps.isEmpty()) {
            for (MKOLUpdateElement bean : downloadedMaps) {
                if (bean.status == 3) {
                    mOffline.start(bean.cityID);
//					threadPoolUtil.downloadCity(bean.cityID);
                }else if (bean.status == 8) {
                    mOffline.remove(bean.cityID);
                    mOffline.start(bean.cityID);
                }
            }
            lAdapter.notify(downloadedMaps);
        }
        initBottomButtonBg();
    }

    /**
     * 获取正在下载或者等待下载的城市数量
     *
     * @return
     */
    private Integer getDownloadingCitys() {
        int downloadingSize = 0;
        downloadedMaps = mOffline.getAllUpdateInfo();
        if (downloadedMaps == null) {
            return downloadingSize;
        }

        if (!downloadedMaps.isEmpty()) {
            for (MKOLUpdateElement bean : downloadedMaps) {
                if (bean.status == 1 || bean.status == 2) {
                    downloadingSize++;
                }
            }
        }
        return downloadingSize;
    }



    /**
     * 更新下载管理列表
     */
    public void updateView() {
        downloadedMaps = mOffline.getAllUpdateInfo();
        if (downloadedMaps == null) {
            downloadedMaps = new ArrayList<MKOLUpdateElement>();
        }
        lAdapter.notify(downloadedMaps);
    }

    private void findView() {

        download_all_tv = (TextView) findViewById(R.id.download_all_tv);
        pause_all_tv = (TextView) findViewById(R.id.pause_all_tv);

        download_all_iv = (ImageView) findViewById(R.id.download_all_iv);
        pause_all_iv = (ImageView) findViewById(R.id.pause_all_iv);
        delete_all_ll = (LinearLayout) findViewById(R.id.delete_all_ll);
        download_all_ll = (LinearLayout) findViewById(R.id.download_all_ll);
        pause_all_ll = (LinearLayout) findViewById(R.id.pause_all_ll);

        download_back = (ImageView) findViewById(R.id.download_back);
        citylist_top_tv1 = (TextView) findViewById(R.id.citylist_top_tv1);
        citylist_top_tv2 = (TextView) findViewById(R.id.citylist_top_tv2);
        downloadedMapsView = (ListView) findViewById(R.id.localmaplist);
        citylist_ll = (LinearLayout) findViewById(R.id.citylist_ll);
        localmap_layout = (RelativeLayout) findViewById(R.id.localmap_layout);
        description_ll = (LinearLayout) findViewById(R.id.description_ll);
        cityExpandable_lv = (ExpandableListView) findViewById(R.id.cityExpandable_lv);
        cityExpandable_lv.setGroupIndicator(null);
        download_back.setOnClickListener(this);
        citylist_top_tv1.setOnClickListener(this);
        citylist_top_tv2.setOnClickListener(this);

        delete_all_ll.setOnClickListener(this);
        download_all_ll.setOnClickListener(this);
        pause_all_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_back:
                finish();
                break;
            case R.id.citylist_top_tv1:// 城市列表
                citylist_ll.setVisibility(View.VISIBLE);
                localmap_layout.setVisibility(View.GONE);
                description_ll.setVisibility(View.GONE);
                // isdownloadpressed = false;
                cityExpandAdapter.notifyDataSetChanged();
                citylist_top_tv1.setBackgroundResource(R.drawable.citylist_press);
                citylist_top_tv2.setBackgroundResource(R.drawable.download_normal);
                citylist_top_tv1.setTextColor(getResources().getColor(
                        R.color.text_new_tag));
                citylist_top_tv2.setTextColor(getResources().getColor(R.color.blue));


                break;
            case R.id.citylist_top_tv2:// 下载管理
                // isdownloadpressed = true;
                switchToDownLoad();
                break;
            case R.id.download_all_ll:// 全部下载点击事件
                startDownLoadAllCitys();
                break;
            case R.id.delete_all_ll:// 全部删除点击事件
                deleteOfflineMapDialog(-1);
                break;
            case R.id.pause_all_ll:// 全部暂停点击事件
                pauseAllUnDownLoadCity();
                break;
            default:
                break;
        }

    }

    /**
     * 切换到下载管理界面
     */

    private void switchToDownLoad() {
        citylist_top_tv1.setBackgroundResource(R.drawable.citylist_normal);
        citylist_top_tv2.setBackgroundResource(R.drawable.download_press);
        citylist_top_tv1.setTextColor(getResources().getColor(R.color.blue));
        citylist_top_tv2.setTextColor(getResources().getColor(
                R.color.text_new_tag));
        updateView();
        if (downloadedMaps.isEmpty()) {
            description_ll.setVisibility(View.VISIBLE);
            citylist_ll.setVisibility(View.GONE);
            localmap_layout.setVisibility(View.GONE);
        } else {
            localmap_layout.setVisibility(View.VISIBLE);
            citylist_ll.setVisibility(View.GONE);
            description_ll.setVisibility(View.GONE);
        }
        initBottomButtonBg();

    }


    @Override
    public void onGetOfflineMapState(int type, int state) {

        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {
                    updateView();
                    cityExpandAdapter.notifyDataSetChanged();
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                break;
            default:
                break;
        }

    }

    /**
     * 下载完成后更新底部UI
     */
    @Override
    public void updateFinished() {
        initBottomButtonBg();
    }


    @Override
    public void networkRecovery() {
        startDownLoadAllCitys();
    }

    @Override
    public void networkShutDown() {
        pauseAllUnDownLoadCity();
    }
}
