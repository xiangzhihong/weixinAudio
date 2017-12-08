package com.example.raojianxiong.recoder.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.raojianxiong.chat.R;

public class RecorderDialog {

    private Dialog mDialog;
    private ImageView mIcon;
    private TextView mLable;
    private TextView mLeftTime;
    private Context mContext;

    public RecorderDialog(Context context) {
        this.mContext = context;
    }

    public void showRecordingDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recorder, null);
        mDialog = new Dialog(mContext, R.style.style_dialog);
        mDialog.setContentView(view);
        mIcon = (ImageView) mDialog.findViewById(R.id.recorder_dialog_icon);
        mLable = (TextView) mDialog.findViewById(R.id.recoder_dialog_label);
        mLeftTime=(TextView) mDialog.findViewById(R.id.recoder_leftTime);
        mDialog.show();
    }

    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setImageResource(R.mipmap.recorder_icon);
            mLable.setText("手指上滑，取消发送");
        }
    }

    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setImageResource(R.mipmap.cancel_recorder_icon);
            mLable.setText("松开手指，取消发送");
        }
    }

    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setImageResource(R.mipmap.voice_to_short);
            mLable.setText("录音时间过短");
        }
    }

    //倒计时提示（10-->0）
    public void recoderConfirm(int time) {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.GONE);
            mLeftTime.setVisibility(View.VISIBLE);
            mLeftTime.setText(time+"");
            mLable.setText("松开手指，取消发送");
        }
    }

    public void dimissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void setVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
            //用switch冗余
            int resId = mContext.getResources().getIdentifier("v"+level,"mipmap",mContext.getPackageName());
        }
    }
}
