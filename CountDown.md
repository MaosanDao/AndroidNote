# 自定义倒计时工具类
```java
package com.upcan.n1.utils;

import android.os.Handler;

/**
 * Comment: 倒计时
 *
 * @author Vangelis.Wang in UpCan
 * @date 2018/7/27
 * Email:Pei.wang@icanup.cn
 */

public class MyCountDownTimer {
    private long millisInFuture;
    private long countDownInterval;
    private boolean status;

    private CountDownListener mListener;

    public interface CountDownListener {
        /**
         * 开始
         */
        void countDownStart();

        /**
         * 完成
         */
        void countDowFinish();

        /**
         * 剩余时间
         *
         * @param time 剩余时间
         */
        void trigger(long time);
    }

    /**
     * 倒计时回调
     */
    public void setCountDownListener(CountDownListener mListener) {
        this.mListener = mListener;
    }

    public MyCountDownTimer(long pMillisInFuture, long pCountDownInterval) {
        this.millisInFuture = pMillisInFuture;
        this.countDownInterval = pCountDownInterval;
        status = false;
        initialize();
    }

    /**
     * 停止
     */
    public void stop() {
        status = false;
    }

    /**
     * 开始
     */
    public void start() {
        status = true;
    }

    public void initialize() {
        final Handler handler = new Handler();
        LogTrack.v("starting");
        if (mListener != null) {
            mListener.countDownStart();
        }
        final Runnable counter = new Runnable() {
            @Override
            public void run() {
                long sec = millisInFuture / 1000;
                if (status) {
                    if (millisInFuture <= 0) {
                        LogTrack.v("done");
                        if (mListener != null) {
                            mListener.countDowFinish();
                        }
                    } else {
                        LogTrack.v(Long.toString(sec) + " seconds remain");
                        if (mListener != null) {
                            mListener.trigger(sec);
                        }
                        millisInFuture -= countDownInterval;
                        handler.postDelayed(this, countDownInterval);
                    }
                } else {
                    handler.removeCallbacks(this);
                }
            }
        };

        handler.postDelayed(counter, countDownInterval);
    }
}
```
