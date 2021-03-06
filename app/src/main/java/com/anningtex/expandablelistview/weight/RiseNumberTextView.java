package com.anningtex.expandablelistview.weight;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author Song
 */
public class RiseNumberTextView extends AppCompatTextView implements RiseNumberBase {
    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private int mPlayingState = STOPPED;
    private float number;
    private float fromNumber;
    private long duration = 1000;
    /**
     * 1.int 2.float
     */
    private int numberType = 2;
    private boolean flags = true;
    private EndListener mEndListener = null;
    final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};

    public RiseNumberTextView(Context context) {
        super(context);
    }

    public RiseNumberTextView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public RiseNumberTextView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    public interface EndListener {
        void onEndFinish();
    }

    public boolean isRunning() {
        return (mPlayingState == RUNNING);
    }

    private void runFloat() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fromNumber, number);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            if (flags) {
                setText(format("##").format(Double.parseDouble(valueAnimator1.getAnimatedValue().toString())) + "");
                if (valueAnimator1.getAnimatedValue().toString().equalsIgnoreCase(number + "")) {
                    setText(format("##0.00").format(Double.parseDouble(number + "")));
                }
            } else {
                setText(format("##0.00").format(Double.parseDouble(valueAnimator1.getAnimatedValue().toString())) + "");
                if (valueAnimator1.getAnimatedValue().toString().equalsIgnoreCase(number + "")) {
                    setText(format("##0.00").format(Double.parseDouble(number + "")));
                }
            }
            if (valueAnimator1.getAnimatedFraction() >= 1) {
                mPlayingState = STOPPED;
                if (mEndListener != null) {
                    mEndListener.onEndFinish();
                }
            }
        });
        valueAnimator.start();
    }

    private void runInt() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) fromNumber, (int) number);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            setText(valueAnimator1.getAnimatedValue().toString());
            if (valueAnimator1.getAnimatedFraction() >= 1) {
                mPlayingState = STOPPED;
                if (mEndListener != null) {
                    mEndListener.onEndFinish();
                }
            }
        });
        valueAnimator.start();
    }

    static int sizeOfInt(int x) {
        for (int i = 0; ; i++) {
            if (x <= sizeTable[i]) {
                return i + 1;
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void start() {
        if (!isRunning()) {
            mPlayingState = RUNNING;
            if (numberType == 1) {
                runInt();
            } else {
                runFloat();
            }
        }
    }

    @Override
    public RiseNumberTextView withNumber(float fromNumber, float number, boolean flag) {
        this.number = number;
        this.flags = flag;
        numberType = 2;
        this.fromNumber = fromNumber;
        return this;
    }

    @Override
    public RiseNumberTextView withNumber(float fromNumber, float number) {
        System.out.println(number);
        this.number = number;
        numberType = 2;
        this.fromNumber = fromNumber;
        return this;
    }

    @Override
    public RiseNumberTextView withNumber(int fromNumber, int number) {
        this.number = number;
        numberType = 1;
        this.fromNumber = fromNumber;
        return this;
    }

    @Override
    public RiseNumberTextView setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public void setOnEnd(EndListener callback) {
        mEndListener = callback;
    }

    /**
     * 格式化
     */
    private DecimalFormat dfs = null;

    public DecimalFormat format(String pattern) {
        if (dfs == null) {
            dfs = new DecimalFormat();
        }
        dfs.setRoundingMode(RoundingMode.FLOOR);
        dfs.applyPattern(pattern);
        return dfs;
    }
}
