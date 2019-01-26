package com.example.z.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MySwitchBtn extends View {

    private Paint mPaint;

    private boolean sChecked = false;

    private boolean sCartooning;

    private RectF mRectF;

    private int mThumbPadding = 5; // the padding between thumb and the background stroke

    private int mColorBackGroundDefault;

    private int mColorBackGroundFocus;

    private int mColorThumb;

    private float mThumbCenterPointX = -1;

    private int REFRESH_FREQUENCY = 5;

    private int CARTOON_STEP_LENGHT = 5;

    public MySwitchBtn(Context context) {
        super(context);
        init(context, null);
    }

    public MySwitchBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MySwitchBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public boolean isChecked() {
        return sChecked;
    }

    private void init(Context ctx, AttributeSet attributeSet) {
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        if (attributeSet != null) {
            TypedArray array = ctx.obtainStyledAttributes(attributeSet, R.styleable.MySwitchBtn);
            mColorBackGroundDefault = array.getInt(R.styleable.MySwitchBtn_colorBackGroundDefault, Color.BLACK);
            mColorBackGroundFocus = array.getInt(R.styleable.MySwitchBtn_colorBackGroundFocus, Color.BLUE);
            mColorThumb = array.getInt(R.styleable.MySwitchBtn_colorThumb, Color.WHITE);
            array.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int radius = Math.min(width, height) / 2;
        if (mThumbCenterPointX == -1) {
            mThumbCenterPointX = radius;
        }
        // 1. draw back
        mRectF.left = 0;
        mRectF.right = width;
        mRectF.top = 0;
        mRectF.bottom = height;
        mPaint.setColor(sChecked ? mColorBackGroundFocus : mColorBackGroundDefault);
        // calculate alpha
        float alpha = 255;
        if (sChecked) {
            alpha = (mThumbCenterPointX / (width - radius)) * alpha;
        } else {
            alpha = ((width - mThumbCenterPointX) / (width - radius)) * alpha;
        }
        int result = alpha > 255 ? 255 : (int) alpha;
        mPaint.setAlpha(result);
        canvas.drawRoundRect(mRectF, radius, radius, mPaint);
        // 2. draw thumb
        mPaint.setAlpha(255);
        mPaint.setColor(mColorThumb);
        if (mThumbCenterPointX < radius) {
            mThumbCenterPointX = radius;
        } else if (mThumbCenterPointX > width - radius) {
            mThumbCenterPointX = width - radius;
        }
        canvas.drawCircle(mThumbCenterPointX, height >> 1, radius - mThumbPadding, mPaint);

        // 3. cartoon
        if (!sCartooning) {
            return;
        }

        if (sChecked) {
            if (mThumbCenterPointX < width - radius) {
                mThumbCenterPointX = mThumbCenterPointX + CARTOON_STEP_LENGHT;
                postDelayed(this::invalidate, REFRESH_FREQUENCY);
            } else {
                sCartooning = false;
            }
        } else {
            if (mThumbCenterPointX > radius) {
                mThumbCenterPointX = mThumbCenterPointX - CARTOON_STEP_LENGHT;
                postDelayed(this::invalidate, REFRESH_FREQUENCY);
            } else {
                sCartooning = false;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (sCartooning) {
            return super.onTouchEvent(event);
        }

        sCartooning = true;

        sChecked = !sChecked;

        invalidate();
        return super.onTouchEvent(event);
    }
}
