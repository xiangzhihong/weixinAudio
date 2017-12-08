package com.example.raojianxiong.recoder.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.raojianxiong.chat.R;

import java.util.Timer;
import java.util.TimerTask;



public class RecoderButton extends Button implements AudioManager.AudioStateListener {

    private static final int DISANCE_Y_CANCEL = 50;
    private static final int STATE_RECORDER_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_CANCEL = 3;
    private static final int MSG_AUDIO_PREPARED = 0X10;
    private static final int MSG_AUDIO_CHANGED = 0X11;
    private static final int MSG_AUDIO_DIMISS = 0X12;
    private static final int MSG_AUDIO_TIME_OUT = 0X13;
    private boolean mReady=false;
    private int mCurState = STATE_RECORDER_NORMAL;
    private boolean isRecording = false;
    private float mTime;
    private float maxTime=5;//最大录制时长
    private int leftTime=10;//录音倒计时，10开始提示
    private Timer timer = new Timer();
    private RecorderDialog dialog;
    private AudioManager audioManager;
    private FinishRecorderListener mListener;

    public RecoderButton(Context context) {
        this(context, null);
    }

    public RecoderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        dialog = new RecorderDialog(getContext());
        String dir = Environment.getExternalStorageDirectory() + "/recorder";//创建文件夹
        audioManager =AudioManager.getInstance(dir);
        audioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                audioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 获取音量大小
     */
    private Runnable mGetVoiceLeveelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_AUDIO_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            leftTime--;
            if (leftTime<=0){
                dialog.dimissDialog();
                return;
            }
            dialog.recoderConfirm(leftTime);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    dialog.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLeveelRunnable).start();
                    break;
                case MSG_AUDIO_CHANGED:
                    dialog.setVoiceLevel(audioManager.getVoiceLevel(7));
                    if (mTime>maxTime){
                        confirmTimer();
                    }
                    break;
                case MSG_AUDIO_DIMISS:
                    dialog.dimissDialog();
                    break;
            }

        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    //根据x y的坐标判断是否想取消
                    if (wantToCancel(x, y)) {
                        changeState(STATE_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime < 0.6f) {
                    System.out.println("录音时间过短");
                    dialog.tooShort();
                    audioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_AUDIO_DIMISS, 1300);
                } else if (mCurState == STATE_RECORDING) {
                    //release callbackto ac
                    System.out.println("正常录制");
                    dialog.dimissDialog();
                    audioManager.release();

                    if (mListener != null) {
                        mListener.onFinish(mTime, audioManager.getCurrentFilePath());
                    }
                } else if (mCurState == STATE_CANCEL) {
                    //cancel
                    System.out.println("取消了");
                    dialog.dimissDialog();
                    audioManager.cancel();
                }
                reset();
                break;

        }
        return super.onTouchEvent(event);
    }

    //恢复状态及标志位
    private void reset() {
        isRecording = false;
        mReady = false;
        mTime = 0;
        changeState(STATE_RECORDER_NORMAL);
    }

    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {//判断手指的横坐标是否超出按钮的范围
            return true;
        }
        //再判断Y
        if (y < -DISANCE_Y_CANCEL || y > getHeight() + DISANCE_Y_CANCEL) {//按钮上部或下部
            return true;
        }
        return false;
    }

    //随着状态的改变，文本颜色和背景改变
    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_RECORDER_NORMAL:
                    setBackgroundResource(R.drawable.btn_recoder_normal);
                    setText(R.string.str_recoder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recoding);
                    setText(R.string.str_recoding);
                    if (isRecording) {
                        dialog.recording();
                    }
                    break;
                case STATE_CANCEL:
                    setBackgroundResource(R.drawable.btn_recoding);
                    setText(R.string.str_recoder_want_cancel);
                    dialog.wantToCancel();
                    break;
            }
        }
    }

    //倒计时定时器
    private void confirmTimer() {
        timer.schedule(new TimerTask() {
            @Override public void run() {
                try {
                    Thread.sleep(1000);
                    handler.sendEmptyMessage(MSG_AUDIO_TIME_OUT);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    /**
     * 录音完成后的回调
     */
    public interface FinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }


    public void setAudioFinishRecorderListener(FinishRecorderListener listener) {
        mListener = listener;
    }

}
