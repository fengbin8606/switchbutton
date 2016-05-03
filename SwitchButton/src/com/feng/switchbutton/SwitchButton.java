package com.feng.switchbutton;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * 自定义开关
 * 
 * @author fengbin
 */
public class SwitchButton extends View implements android.view.View.OnClickListener {
    private Bitmap mSwitchBottomOn, mSwitchBottomOff, mSwitchBtnOn, mSwitchBtnOff;
    private float mCurrentX = 0;
    private boolean mSwitchOn = true;// 开关默认是开着的
    private int mMoveLength;// 最大移动距离
    private float mLastX = 0;// 第一次按下的有效区域

    private Rect mDest = null;// 绘制的目标区域大小
    private Rect mSrc = null;// 截取源图片的大小
    private int mDeltX = 0;// 移动的偏移量
    private Paint mPaint = null;
    private OnChangeListener mListener = null;
    private boolean mFlag = false;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化相关资源
     */
    public void init() {
        mSwitchBottomOn = BitmapFactory.decodeResource(getResources(),
                R.drawable.switch_bottom_on);
        mSwitchBottomOff = BitmapFactory.decodeResource(getResources(),
                R.drawable.switch_bottom_off);
        mSwitchBtnOn = BitmapFactory.decodeResource(getResources(),
                R.drawable.switch_btn_on);
        mSwitchBtnOff = BitmapFactory.decodeResource(getResources(),
                R.drawable.switch_btn_off);
        setOnClickListener(this);
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        // 最大移动距离
        mMoveLength = mSwitchBottomOn.getWidth() - mSwitchBtnOn.getWidth();

        mDest = new Rect(0, 0, mSwitchBottomOn.getWidth(), mSwitchBottomOn.getHeight());
        mSrc = new Rect();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(255);
        mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSwitchBottomOn.getWidth(), mSwitchBottomOn.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int count = canvas.saveLayer(new RectF(mDest), null, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        if (mDeltX > 0 || mDeltX == 0 && mSwitchOn) {

            canvas.drawBitmap(mSwitchBottomOn, 0, 0, null);

            mSrc.set(mSwitchBottomOn.getWidth() - mSwitchBtnOn.getWidth(),
                    0,
                    mSwitchBottomOn.getWidth(),
                    mSwitchBtnOn.getHeight()
                    );
            canvas.drawBitmap(mSwitchBtnOn, mDest, mSrc, null);

        }
        else if (mDeltX < 0 || mDeltX == 0 && !mSwitchOn) {

            canvas.drawBitmap(mSwitchBottomOff, 0, 0, null);

            mSrc.set(0,
                    0,
                    mSwitchBtnOff.getWidth(),
                    mSwitchBtnOff.getHeight()
                    );
            canvas.drawBitmap(mSwitchBtnOff, mDest, mSrc, null);

        }

        canvas.restoreToCount(count);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentX = event.getX();
                mDeltX = (int) (mCurrentX - mLastX);
                // 如果开关开着向左滑动，或者开关关着向右滑动（这时候是不需要处理的）
                if ((mSwitchOn && mDeltX < 0) || (!mSwitchOn && mDeltX > 0)) {
                    mFlag = true;
                    mDeltX = 0;
                }

                if (Math.abs(mDeltX) > mMoveLength) {
                    mDeltX = mDeltX > 0 ? mMoveLength : -mMoveLength;
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                if (Math.abs(mDeltX) > 0 && Math.abs(mDeltX) < mMoveLength / 2) {
                    mDeltX = 0;
                    invalidate();
                    return true;
                } else if (Math.abs(mDeltX) > mMoveLength / 2 && Math.abs(mDeltX) <= mMoveLength) {
                    mDeltX = mDeltX > 0 ? mMoveLength : -mMoveLength;
                    mSwitchOn = !mSwitchOn;
                    if (mListener != null) {
                        mListener.onChange(this, mSwitchOn);
                    }
                    invalidate();
                    mDeltX = 0;
                    return true;
                } else if (mDeltX == 0 && mFlag) {
                    // 这时候得到的是不需要进行处理的，因为已经move过了
                    mDeltX = 0;
                    mFlag = false;
                    return true;
                }
                return super.onTouchEvent(event);
            default:
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    public void setOnChangeListener(OnChangeListener listener) {
        mListener = listener;
    }

    public interface OnChangeListener {
        public void onChange(SwitchButton sb, boolean state);
    }

    @Override
    public void onClick(View v) {
        mDeltX = mSwitchOn ? mMoveLength : -mMoveLength;
        mSwitchOn = !mSwitchOn;
        if (mListener != null) {
            mListener.onChange(this, mSwitchOn);
        }
        invalidate();
        mDeltX = 0;
    }
}
