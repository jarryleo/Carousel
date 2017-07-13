package cn.leo.carousel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by JarryLeo on 2017/3/1.
 */

public class ViewPagerPoint extends View implements ViewPager.OnPageChangeListener {
    //背景点个数
    private int maxPoint = 5;
    //当前选中的点
    private int currentIndex = 0;
    //背景点的大小,单位dp
    private float backPointSize = 10;
    //选中点的大小,单位dp
    private float forePointSize = 10;
    //点之间的距离,单位dp
    private float pointDistance = 10;
    //关联的viewPager
    private ViewPager mVP;
    //背景点的颜色
    private int mBackPointColor = Color.argb(128, 0, 0, 0);
    //选中点的颜色
    private int mForePointColor = Color.RED;
    //本控件的宽
    private float mWidth;
    //本控件的高
    private float mHeight;
    //画笔
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //滑动距离百分比
    private float mFraction;
    private OnPointClickListener mListener;
    private long mDownTime;
    private boolean mAnimation = true;
    private boolean mBignum = true;
    private float mBackSize;
    private float mForeSize;
    private float mDistance;

    public ViewPagerPoint(Context context) {
        this(context, null);

    }

    public ViewPagerPoint(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerPoint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, displayMetrics);
//        float density = getResources().getDisplayMetrics().density;
        //实际点大小dp
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mBackSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, backPointSize, displayMetrics);
        mForeSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, forePointSize, displayMetrics);
        mDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pointDistance, displayMetrics);
        //设置本控件宽高
        mHeight = mBackSize > mForeSize ? mBackSize : mForeSize;
        mWidth = (maxPoint - 1) * (mDistance + mBackSize) + mForeSize;

        int width = MeasureSpec.makeMeasureSpec((int) (mWidth + 0.5f), MeasureSpec.EXACTLY);
        int height = MeasureSpec.makeMeasureSpec((int) (mHeight + 0.5f), MeasureSpec.EXACTLY);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {


        //画背景点
        paint.setColor(mBackPointColor);
        for (int i = 0; i < maxPoint; i++) {
            float y = mHeight / 2;
            float x = y + (mBackSize + mDistance) * i;
            float r = mBackSize / 2;
            canvas.drawCircle(x, y, r, paint);
        }
        //画选中点
        paint.setColor(mForePointColor);
        if (mVP == null) {
            float y = mHeight / 2;
            float x = y + (mBackSize + mDistance) * currentIndex;
            float r = mForeSize / 2;
            canvas.drawCircle(x, y, r, paint);
        } else {
            //滑动动画
            float y = mHeight / 2;
            float x = y + (mBackSize + mDistance) * (currentIndex + mFraction);
            float r = mForeSize / 2;
            canvas.drawCircle(x, y, r, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mListener == null) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = SystemClock.uptimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                if (SystemClock.uptimeMillis() - mDownTime < 1000) {
                    int index = (int) (x / (mHeight - 2) / 2);
                    mListener.onPointClick(index);
                }
                break;
        }
        return true;
    }

    /**
     * 设置点的个数
     *
     * @param maxPoint
     */
    public void setMaxPoint(int maxPoint) {
        this.maxPoint = maxPoint;
    }

    /**
     * 设置点的大小
     *
     * @param size
     */
    public void setBackPointSize(float size) {
        backPointSize = size;
    }

    /**
     * 设置前景点大小
     *
     * @param forePointSize
     */
    public void setForePointSize(float forePointSize) {
        this.forePointSize = forePointSize;
    }

    /**
     * 点之间的间隔
     *
     * @param pointDistance
     */
    public void setPointDistance(float pointDistance) {
        this.pointDistance = pointDistance;
    }

    /**
     * 设置关联的ViewPager,能随着viewpager的滑动自动切换点,
     * 无限滑动的Viewpager请勿设置此项
     *
     * @param viewPager
     */
    public void attachViewPager(ViewPager viewPager) {
        mVP = viewPager;
        mVP.addOnPageChangeListener(this);
        PagerAdapter adapter = mVP.getAdapter();
        maxPoint = adapter.getCount();
        //点的总个数不能超过20
        if (maxPoint > 20) {
            throw new IndexOutOfBoundsException("小圆点不能超过20个");
        }
        invalidate();
    }

    /**
     * 绑定ViewPager,同时设置背景点个数
     * 此方法适合无限滑动的ViewPager
     *
     * @param viewPager
     * @param maxPoint
     * @param bigNum    是否为大整数无限方式 ，否的话为前后加一张循环
     */
    public void attachViewPager(ViewPager viewPager, int maxPoint, boolean bigNum) {
        mVP = viewPager;
        mVP.addOnPageChangeListener(this);
        this.maxPoint = maxPoint;
        this.mBignum = bigNum;
        invalidate();
    }

    /**
     * 大整数循环
     *
     * @param viewPager
     * @param maxPoint
     */
    public void attachViewPager(ViewPager viewPager, int maxPoint) {
        attachViewPager(viewPager, maxPoint, true);
    }

    /**
     * 设置当前选中的点
     *
     * @param index
     */
    public void setCurrentIndex(int index) {
        currentIndex = index % maxPoint;
        invalidate();
    }

    /**
     * 设置背景点的颜色
     *
     * @param color
     */
    public void setBackPointColor(int color) {
        mBackPointColor = color;
        invalidate();
    }

    /**
     * 设置选中点的颜色
     *
     * @param color
     */
    public void setForePointColor(int color) {
        mForePointColor = color;
        invalidate();
    }

    /**
     * 滑动动画
     *
     * @param animation
     */
    public void setAnimation(boolean animation) {
        mAnimation = animation;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (!mAnimation) {
            return;
        }
        mFraction = positionOffset;
        if (mBignum) {
            currentIndex = position % maxPoint;
        } else {
            currentIndex = position - 1;
        }
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        mFraction = 0;
        if (mBignum) {
            currentIndex = position % maxPoint;
        } else {
            currentIndex = position - 1;
        }
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 给小圆点设置点击监听事件
     */

    public void setOnPointClickListener(OnPointClickListener listener) {
        mListener = listener;
    }

    /**
     * 圆点单击事件
     */
    public interface OnPointClickListener {
        void onPointClick(int pointIndex);
    }
}
