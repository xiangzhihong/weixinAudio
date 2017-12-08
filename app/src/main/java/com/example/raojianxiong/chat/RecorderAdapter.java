package com.example.raojianxiong.chat;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.raojianxiong.bean.Recorder;

import java.util.List;

public class RecorderAdapter extends ArrayAdapter<Recorder> {

    private List<Recorder> mDatas;
    private Context mContext;
    private int mMinItemWidth;
    private int mMaxItemWidth;

    public RecorderAdapter(Context context, List<Recorder> mDatas) {
        super(context, -1,mDatas);
        mContext = context;
        this.mDatas = mDatas;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        mMaxItemWidth = (int) (dm.widthPixels*0.7f);
        mMinItemWidth = (int) (dm.widthPixels*0.15f);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_recorder,parent,false);
               holder = new ViewHolder();
            holder.seconds = (TextView) convertView.findViewById(R.id.id_recorder_time);
            holder.length = convertView.findViewById(R.id.id_recorder_length);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.seconds.setText(Math.round(getItem(position).time)+"\"");

        ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
        lp.width = (int) (mMinItemWidth + (mMaxItemWidth/60f*getItem(position).time));

        return convertView;
    }
    private class  ViewHolder{
        TextView seconds;
        View length;
    }
}
