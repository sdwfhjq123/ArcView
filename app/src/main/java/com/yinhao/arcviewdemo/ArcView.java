package com.yinhao.arcviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.security.PublicKey;
import java.util.List;

/**
 * Created by yinhao on 2018/1/5.
 */

public class ArcView extends View {
    private static final String TAG = "ArcView";

    private int mHeight, mWidth;//宽高
    private Paint mPaint;//扇形的画笔
    private Paint mTextPaint;//画文字的画笔

    private int centerX, centerY;//中心坐标

    //"其他"value
    //扇形图分成太多块，
    private double rest;

    private int maxNum;//扇形图的最大块数，超过的item就合并到其他

    String others = "其他";
    double total;//数据的总和
    double[] datas;//数据集
    String[] texts;//每个数据对应的文字集

    //颜色 默认的颜色
    private int[] mColors = {
            Color.parseColor("#FF4081"), Color.parseColor("#ffc0cb"),
            Color.parseColor("#00ff00"), Color.parseColor("#0066ff"), Color.parseColor("#ffee00")
    };

    private int mTextSize;
    private int radius = 500;//半径

    public ArcView(Context context) {
        this(context, null);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(40);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setColor(Color.BLACK);

        mTextSize = 30;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //无数据直接返回
        if (datas == null || datas.length == 0) return;

        centerX = (getRight() - getLeft()) / 2;
        centerY = (getBottom() - getTop()) / 2;

        int min = mHeight > mWidth ? mWidth : mHeight;
        if (radius > min / 2) {
            radius = (int) ((min - getPaddingTop() - getPaddingBottom()) / 3);
        }

        //画扇形
        canvas.save();
        drawArc(canvas);
        canvas.restore();

        canvas.save();
        drawLineAndText(canvas);
        canvas.restore();
    }

    private void drawLineAndText(Canvas canvas) {
        int startAngle = 0;
        canvas.translate(centerX, centerY);//平移画布到中心
        mPaint.setStrokeWidth(4);
        for (int i = 0; i < (maxNum < datas.length ? maxNum : datas.length); i++) {
            float sweepAngles = (float) ((datas[i] * 1.0f / total) * 360);
            drawLine(canvas, startAngle, sweepAngles, texts[i], mColors[i % mColors.length]);
            startAngle += sweepAngles;
        }

        //画其他
        if (startAngle < 360) {
            drawLine(canvas, startAngle, 360 - startAngle, others, Color.GRAY);
        }
    }

    private void drawLine(Canvas canvas, int startAngle, float sweepAngles, String text, int color) {
        mPaint.setColor(color);
        float stopX, stopY;//中心点
        stopX = (float) ((radius + 40) * Math.cos((2 * startAngle + sweepAngles) / 2 * Math.PI / 180));
        stopY = (float) ((radius + 40) * Math.sin((2 * startAngle + sweepAngles) / 2 * Math.PI / 180));

        canvas.drawLine((float) ((radius - 20) * Math.cos((2 * startAngle + sweepAngles) / 2 * Math.PI / 180)),
                (float) ((radius - 20) * Math.sin((2 * startAngle + sweepAngles) / 2 * Math.PI / 180)), stopX, stopY, mPaint);

        Log.e(TAG, "中心点:(" + stopX + "," + stopY + ")");
        //画横线
        int endX;
        if (stopX > 0) {
            endX = (centerX - getPaddingRight() - 30);
        } else {
            endX = (-centerX + getPaddingLeft() + 30);
        }

        canvas.drawLine(stopX, stopY, endX, stopY, mPaint);

        //测量文字大小
        Rect rect = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), rect);
        int w = rect.width();
        int h = rect.height();
        int offset = 30;//文字在横线上的偏移量
        //画文字
        canvas.drawText(text, 0, text.length(), stopX > 0 ? stopX + offset : stopX - w - offset, stopY + h, mTextPaint);

        //测量百分比大小
        String percentage = sweepAngles / 3.60 + "";
        percentage = percentage.substring(0, percentage.length() > 4 ? 4 : percentage.length()) + "%";
        mTextPaint.getTextBounds(percentage, 0, percentage.length(), rect);

        //画百分比
        canvas.drawText(percentage, 0, percentage.length(), stopX > 0 ? stopX + offset : stopX - w - offset, stopY - 5, mTextPaint);
    }

    private void drawArc(Canvas canvas) {
        RectF rectF = new RectF((float) (centerX - radius), centerY - radius, centerX + radius, centerY + radius);

        int startAngle = 0;//开始的角度
        for (int i = 0; i < (maxNum < datas.length ? maxNum : datas.length); i++) {
            float sweepAngles = (float) ((datas[i] * 1.0f / total) * 360);
            mPaint.setColor(mColors[i % mColors.length]);
            canvas.drawArc(rectF, startAngle, sweepAngles, true, mPaint);
            startAngle += sweepAngles;
        }

        //画其他
        rest = 0;
        for (int i = maxNum; i < datas.length; i++) {
            rest += datas[i];
        }

        float sweepAngle = (float) 360 - startAngle;
        mPaint.setColor(Color.GRAY);
        canvas.drawArc(rectF, startAngle, sweepAngle, true, mPaint);

    }


    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public void setOthersText(String others) {
        this.others = others;
    }

    public void setColors(int[] colors) {
        mColors = colors;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public abstract class ArcViewAdapter<T> {

        public void setData(List<T> list) {
            datas = new double[list.size()];
            texts = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                total += getValue(list.get(i));
                datas[i] = getValue(list.get(i));
                texts[i] = getText(list.get(i));
            }
        }

        //通过传来的数据集的某个元素，得到具体的数字
        public abstract double getValue(T t);

        //通过传来的数据集的某个原色，得到具体的描述
        public abstract String getText(T t);
    }
}
