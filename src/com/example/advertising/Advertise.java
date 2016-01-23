package com.example.advertising;

import java.util.Timer;
import java.util.TimerTask;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class Advertise implements Runnable{
	private static final String TAG = "MainActivity";
	private Context context;
	
	private ViewPager mBanner;
	private BannerAdapter mBannerAdapter;
	private ImageView[] mIndicators;
	private Timer mTimer = new Timer();
	
	private int mBannerPosition = 0;
	private final int FAKE_BANNER_SIZE = 100;
	private final int DEFAULT_BANNER_SIZE = 5;
	private boolean mIsUserTouched = false;
	
	private int[] mImagesSrc;
	
	public Advertise(Context context,ViewPager mBanner,int[] mImagesSrc,
			ImageView[] mIndicators,boolean mIsUserTouched) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.mBanner=mBanner;
		this.mImagesSrc=mImagesSrc;
		this.mIndicators=mIndicators;
		this.mIsUserTouched=mIsUserTouched;
	}
	
	public void startPlay(){
		mBannerAdapter = new BannerAdapter();
		mBanner.setAdapter(mBannerAdapter);
		mBanner.setOnPageChangeListener(mBannerAdapter);
		mBanner.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN
						|| action == MotionEvent.ACTION_MOVE) {
					mIsUserTouched = true;
				} else if (action == MotionEvent.ACTION_UP) {
					mIsUserTouched = false;
				}
				return false;
			}
		});
		mTimer.schedule(mTimerTask, 5000, 5000);
	}
	
	public void stopPlay(){
		mIsUserTouched=false;
	}


	private TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (!mIsUserTouched) {
				mBannerPosition = (mBannerPosition + 1) % FAKE_BANNER_SIZE;
				Log.d("运行线程：", "tname:" + Thread.currentThread().getName());
			}
		}
	};

	public class BannerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private LayoutInflater mInflater;

        public BannerAdapter() {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return FAKE_BANNER_SIZE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position %= DEFAULT_BANNER_SIZE;
            View view = mInflater.inflate(R.layout.item, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setImageResource(mImagesSrc[position]);
            final int pos = position;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "click banner item :" + pos, Toast.LENGTH_SHORT).show();
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            int position = mBanner.getCurrentItem();
            Log.d(TAG, "finish update before, position=" + position);
            if (position == 0) {
                position = DEFAULT_BANNER_SIZE;
                mBanner.setCurrentItem(position, false);
            } else if (position == FAKE_BANNER_SIZE - 1) {
                position = DEFAULT_BANNER_SIZE - 1;
                mBanner.setCurrentItem(position, false);
            }
            Log.d(TAG, "finish update after, position=" + position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mBannerPosition = position;
            setIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
	
	private void setIndicator(int position) {
        position %= DEFAULT_BANNER_SIZE;
        for(ImageView indicator : mIndicators) {
            indicator.setImageResource(R.mipmap.indicator_unchecked);
        }
        mIndicators[position].setImageResource(R.mipmap.indicator_checked);
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (mBannerPosition == FAKE_BANNER_SIZE - 1) {
            mBanner.setCurrentItem(DEFAULT_BANNER_SIZE - 1, false);
        } else {
            mBanner.setCurrentItem(mBannerPosition);
        }
	}
}
