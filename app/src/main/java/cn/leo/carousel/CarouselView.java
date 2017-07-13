package cn.leo.carousel;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Leo on 2017/7/12.
 */

public class CarouselView extends FrameLayout {
    private List<String> mImageList;
    private innerViewPager mViewPager;
    private ImageAdapter mAdapter;
    private boolean mAutoScroll = true;
    private ImageLoader mImageLoader;
    private Handler mHandler;
    private int mScrollInterval = 3000; //自动轮播间隔3秒
    private int mCurrentItem;

    public CarouselView(Context context) {
        this(context, null);
    }

    public CarouselView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 必须实现图片加载功能
     *
     * @param imageLoader
     * @return
     */
    public CarouselView initImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        return this;
    }

    public void setImageList(List<String> imageList) {
        mImageList = imageList;
        if (mViewPager == null) {
            mViewPager = new innerViewPager(getContext());
            this.addView(mViewPager);
            mViewPager.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mAdapter = new ImageAdapter();
            mViewPager.setAdapter(mAdapter);
            mCurrentItem = mImageList.size() * 50000;
            mViewPager.setCurrentItem(mCurrentItem);
            mHandler = new Handler(Looper.getMainLooper());
        } else {
            mAdapter.notifyDataSetChanged();
        }
        if (mAutoScroll) {
            openAutoScroll();
        }
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    /**
     * 设置自动轮播间隔时长
     *
     * @param interval 单位 ms
     */
    public void setScrollInterval(int interval) {
        mScrollInterval = interval;
    }

    /**
     * 开启自动轮播
     */
    public void openAutoScroll() {
        mAutoScroll = true;
        autoScroll();
    }

    /**
     * 关闭自动轮播
     */
    public void closeAutoScroll() {
        mAutoScroll = false;
        mHandler.removeCallbacks(autoScrollMission);
    }

    private void autoScroll() {
        mHandler.removeCallbacks(autoScrollMission);
        mHandler.postDelayed(autoScrollMission, mScrollInterval);
    }

    private Runnable autoScrollMission = new Runnable() {

        @Override
        public void run() {
            mCurrentItem++;
            if (mCurrentItem > mImageList.size() * 100000L) {
                mCurrentItem = mImageList.size() * 50000 + 1;
            }
            mViewPager.setCurrentItem(mCurrentItem);
            autoScroll();
        }
    };

    private class ImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mImageList != null) {
                return mImageList.size() * 100000;
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int i = position % mImageList.size();
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            container.addView(imageView);
            picLoader(imageView, mImageList.get(i));
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private void picLoader(ImageView imageView, String imagePath) {
            if (mImageLoader != null) {
                mImageLoader.loadImage(imageView, imagePath);
            }
        }

    }

    private class innerViewPager extends ViewPager {

        public innerViewPager(Context context) {
            this(context, null);
        }

        public innerViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (!mAutoScroll) return super.onTouchEvent(ev);
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    //暂时关闭自动滑动
                    mHandler.removeCallbacks(autoScrollMission);
                    break;
                case MotionEvent.ACTION_UP:
                    //开启自动滑动
                    autoScroll();
                    break;
            }
            return super.onTouchEvent(ev);
        }
    }


    /**
     * 图片加载器用户自己实现
     */
    public interface ImageLoader {
        void loadImage(ImageView imageView, String imagePath);
    }
}
