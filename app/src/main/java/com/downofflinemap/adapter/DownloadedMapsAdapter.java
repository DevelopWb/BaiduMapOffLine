package com.downofflinemap.adapter;

/**
 * Author:wang_sir
 * Time:2018/6/1 11:33
 * Description:This is DownloadedMapsAdapter
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.downofflinemap.R;
import com.downofflinemap.interfaces.UpdateFinishedCallBack;

import java.util.ArrayList;

/**
 * 离线地图管理列表适配器
 */
public class DownloadedMapsAdapter extends BaseAdapter {
    private ArrayList<MKOLUpdateElement> downloadedMaps;
    private UpdateFinishedCallBack updateFinishedCallBack;
    private Context context;

    public DownloadedMapsAdapter(Context context, ArrayList<MKOLUpdateElement> downloadedMaps, UpdateFinishedCallBack updateFinishedCallBack) {
        this.downloadedMaps = downloadedMaps;
        this.updateFinishedCallBack = updateFinishedCallBack;
        this.context = context;
    }

    public void notify(ArrayList<MKOLUpdateElement> downloadedMaps){
        this.downloadedMaps = downloadedMaps;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return downloadedMaps == null ? 0 : downloadedMaps.size();
    }

    @Override
    public Object getItem(int index) {
        return downloadedMaps.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup arg2) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.download_item, null);
            holder = new ViewHolder();
            holder.cityname_tv = (TextView) view
                    .findViewById(R.id.cityname_tv);
            holder.citysize_tv = (TextView) view
                    .findViewById(R.id.citysize_tv);
            holder.cityprogress_tv = (TextView) view
                    .findViewById(R.id.cityprogress_tv);
            holder.citydelete_tv = (TextView) view
                    .findViewById(R.id.citydelete_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        MKOLUpdateElement e = downloadedMaps.get(index);

        initViewItem(holder, e);
        return view;
    }

    public void initViewItem(ViewHolder holder, final MKOLUpdateElement e) {

        holder.cityprogress_tv.setText(e.ratio + "%");
        holder.cityname_tv.setText(e.cityName);
        holder.citysize_tv.setText(formatDataSize(e.size));
        if (e.status == 1) {// 正在下载
            holder.citydelete_tv.setText("暂停下载");
        } else if (e.status == 2) {// 等待下载
            holder.citydelete_tv.setText("等待下载");
        } else if (e.status == 3||e.status == 8) {// 已暂停
            holder.citydelete_tv.setText("已暂停");
        } else if (e.status == 4 || e.status == 10) {// 下载完成
            holder.citydelete_tv.setText("已完成");
            holder.cityprogress_tv.setText("");

            if (updateFinishedCallBack != null) {
                updateFinishedCallBack.updateFinished();
            }

        }
    }

    class ViewHolder {

        private TextView cityname_tv;
        private TextView citysize_tv;
        private TextView cityprogress_tv;
        private TextView citydelete_tv;

        public ViewHolder() {

        }


    }
    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }
}

