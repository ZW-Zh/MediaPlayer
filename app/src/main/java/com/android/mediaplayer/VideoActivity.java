package com.android.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.mediaplayer.utils.DensityUtils;

import java.io.File;

import static com.android.mediaplayer.utils.DensityUtils.dip2px;

/**
 * Created by zzw on 2019/2/8.
 */

public class VideoActivity extends Activity {
    private VideoView videoView;
    private SeekBar seekBar;
    private ImageView playController, screenImg, mOperationBg,back,move,goback;
    private TextView currentTimeTv, totalTimeTv,title;
    private static final int updateUI = 1;
    private AudioManager audioManager;
    private boolean isFullScreen = false;
    private LinearLayout controlLayout;
    private boolean isEMove = false;
    private int screen_height;
    private int screen_width;
    private int Num = 54;
    private float lastX = 0, lastY = 0;
    private View mOperationPercent;
    private LinearLayout mFlContent;
    private int videoWidth,videoHeight;
    private float absMoveX,absMoveY;
    private long currentMS;
    private RelativeLayout titleLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);

        initUi();
        setPlayerEvent();
        Intent intent=getIntent();
        String filePath=intent.getStringExtra("path");
        String titleStr=intent.getStringExtra("title");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title.setText(titleStr);
        videoView.setVideoPath(filePath);
        videoView.start();
        System.out.println(videoView.getDuration());
        UIhandle.sendEmptyMessage(updateUI);

    }

    private void updateTime(TextView textView, int totalTime) {
        int second = totalTime / 1000;
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String time = null;
        if (hh != 0) {
            time = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            time = String.format("%02d:%02d", mm, ss);
        }
        textView.setText(time);
    }

    private Handler UIhandle = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {

            if (message.what == updateUI) {
                int currentPostion = videoView.getCurrentPosition();
                int totalDuration = videoView.getDuration();
                updateTime(currentTimeTv, currentPostion);
                updateTime(totalTimeTv, totalDuration);
                seekBar.setMax(totalDuration);
                seekBar.setProgress(currentPostion);
                UIhandle.sendEmptyMessageDelayed(updateUI, 100);

            }
            return false;
        }
    });


    private void setPlayerEvent() {
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = videoView.getCurrentPosition();
                pos += 10000; // milliseconds
                videoView.seekTo(pos);
                seekBar.setProgress(pos);
                UIhandle.sendEmptyMessage(updateUI);
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = videoView.getCurrentPosition();
                pos -= 2000; // milliseconds
                videoView.seekTo(pos);
                seekBar.setProgress(pos);
                UIhandle.sendEmptyMessage(updateUI);
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoHeight=mediaPlayer.getVideoHeight();
                videoWidth=mediaPlayer.getVideoWidth();
                setVideoViewScale(videoHeight,ViewGroup.LayoutParams.MATCH_PARENT);
            }
        });



        videoView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //现在的x,y坐标
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    //手指按下:
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        currentMS = System.currentTimeMillis();

                        break;
                    //手指移动:
                    case MotionEvent.ACTION_MOVE:
                        //偏移量
                        float moveX = x - lastX;
                        float moveY = y - lastY;
                        //计算绝对值
                         absMoveX = Math.abs(moveX);
                         absMoveY = Math.abs(moveY);
                        //手势合法性的验证
                        if (absMoveX > Num && absMoveY > Num) {
                            if (absMoveX < absMoveY) {
                                isEMove = true;
                            } else {
                                isEMove = false;
                            }
                        } else if (absMoveX < Num && absMoveY > Num) {
                            isEMove = true;
                        } else if (absMoveX > Num && absMoveY < Num) {
                            isEMove = false;
                        }

                        /**
                         * 区分手势合法的情况下，区分是去调节亮度还是去调节声音
                         */
                        if (isEMove) {
                            //手势在左边
                            if (x < screen_width / 2) {
                                //Log.e("Emove", "onTouch: " + "手势在左边");
                                /**
                                 * 调节亮度
                                 */
                                changeBright(-moveY);
                                //手势在右边
                            } else {
                                //Log.e("Emove", "onTouch: " + "手势在右边");
                                /**
                                 * 调节音量
                                 */
                                changeVolume(-moveY);
                            }
                        }

                        lastX = x;
                        lastY = y;
                        break;
                    //手指抬起:
                    case MotionEvent.ACTION_UP:
                        long moveTime = System.currentTimeMillis() - currentMS;
                        if(moveTime>200){
                            return true; //不再执行后面的事件，在这句前可写要执行的触摸相关代码。点击事件是发生在触摸弹起后
                        }else {
                            if(controlLayout.getVisibility()==View.INVISIBLE){
                                controlLayout.setVisibility(View.VISIBLE);
                            }else {
                                controlLayout.setVisibility(View.INVISIBLE);
                            }
                            if(titleLayout.getVisibility()==View.INVISIBLE){
                                titleLayout.setVisibility(View.VISIBLE);
                            }else {
                                titleLayout.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;
                }
                return true;
            }
        });

        playController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    //暂停播放
                    playController.setImageResource(R.drawable.stop);
                    videoView.pause();
                    UIhandle.removeMessages(updateUI);
                } else {
                    //继续播放
                    playController.setImageResource(R.drawable.run);
                    videoView.start();
                    UIhandle.sendEmptyMessage(updateUI);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateTime(currentTimeTv, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UIhandle.removeMessages(updateUI);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                videoView.seekTo(progress);
                UIhandle.sendEmptyMessage(updateUI);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playController.setImageResource(R.drawable.stop);
            }
        });
        screenImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFullScreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                isFullScreen=!isFullScreen;
            }
        });


    }

    private void initUi() {
        videoView = findViewById(R.id.videoView);
        seekBar = findViewById(R.id.pos_seekBar);
        playController = findViewById(R.id.stop);
        screenImg = findViewById(R.id.fullscreen);
        currentTimeTv = findViewById(R.id.current_time_tv);
        totalTimeTv = findViewById(R.id.total_time);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        controlLayout = findViewById(R.id.control_layout);
        screen_width = getResources().getDisplayMetrics().widthPixels;
        screen_height = getResources().getDisplayMetrics().heightPixels;
        mOperationPercent = findViewById(R.id.operation_percent);
        mFlContent = findViewById(R.id.fl_content);
        mOperationBg = findViewById(R.id.operation_bg);
        titleLayout=findViewById(R.id.title_layout);
        title=findViewById(R.id.title);
        back=findViewById(R.id.back);
        move=findViewById(R.id.move);
        goback=findViewById(R.id.goback);
    }

    private void setVideoViewScale(int height,int width) {

        ViewGroup.LayoutParams layoutParams =videoView.getLayoutParams();

        layoutParams.height = height;
        layoutParams.width=width;
        videoView.setLayoutParams(layoutParams);
    }

    /**
     * 监听屏幕方向改变
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //横屏
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
//            isFullScreen = true;
//            setVideoViewScale(videoHeight,videoWidth);
//            controlLayout.setVisibility(View.INVISIBLE);
//            getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));
//            getWindow().addFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
//
//        } else {
//            getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);
//            setVideoViewScale(videoHeight,videoWidth);
//            isFullScreen = false;
//            controlLayout.setVisibility(View.VISIBLE);
//            getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
//            getWindow().addFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));
//
//        }
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dip2px(this,235f));
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            videoView.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoView.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        }


    }


    /**
     * 调节音量:偏移量和音量值的换算
     */
    private void changeVolume(float moveY) {
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int index = (int) (moveY / screen_height * max * 4);
        int volume = Math.max(current + index, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        if (mFlContent.getVisibility() == View.GONE) {
            mFlContent.setVisibility(View.VISIBLE);
        }
        mOperationBg.setImageResource(R.drawable.volume);
        ViewGroup.LayoutParams layoutParams = mOperationPercent.getLayoutParams();
        layoutParams.width = (int) (dip2px(this, 94) * (float) volume / max);
        mOperationPercent.setLayoutParams(layoutParams);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFlContent.setVisibility(View.GONE);
            }
        }, 1000);
    }

    /**
     * 调节亮度:
     */
    private void changeBright(float moveY) {
        float mBrightness = 0;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        mBrightness = layoutParams.screenBrightness;
        if(mBrightness==-1){
            mBrightness=getSystemBrightness()/255f;
        }
        float index = moveY / screen_height / 2;
        mBrightness += index;
        //做临界值的判断
        if (mBrightness > 1.0f) {
            mBrightness = 1.0f;
        }
        if (mBrightness < 0.01) {
            mBrightness = 0.01f;
        }
        layoutParams.screenBrightness = mBrightness;
        getWindow().setAttributes(layoutParams);
        if (mFlContent.getVisibility() == View.GONE){
            mFlContent.setVisibility(View.VISIBLE);
        }
        mOperationBg.setImageResource(R.drawable.bright);
        ViewGroup.LayoutParams newlayoutParams = mOperationPercent.getLayoutParams();
        newlayoutParams.width = (int) (dip2px(this, 94) * mBrightness);
        mOperationPercent.setLayoutParams(newlayoutParams);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFlContent.setVisibility(View.GONE);
            }
        }, 1000);
    }

    /**
     * 获得系统亮度
     *
     * @return
     */
    private int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("1", "pause");
        UIhandle.removeMessages(updateUI);
        playController.setImageResource(R.drawable.stop);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("1", "resume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        int progress = seekBar.getProgress();
        videoView.seekTo(progress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("1", "destory");
    }

}
