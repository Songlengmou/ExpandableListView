package com.anningtex.expandablelistview.weight;

/**
 * @author Song
 * desc：数字动画自定义
 */
public interface RiseNumberBase {

    void start();

    RiseNumberTextView withNumber(float fromNumber, float number);

    RiseNumberTextView withNumber(float fromNumber, float number, boolean flag);

    RiseNumberTextView withNumber(int fromNumber, int number);

    RiseNumberTextView setDuration(long duration);

    void setOnEnd(RiseNumberTextView.EndListener callback);
}
