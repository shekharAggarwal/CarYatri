package com.caryatri.caryatri;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.caryatri.caryatri.Common.Common;
import com.caryatri.caryatri.adapter.FullImageAdapter;

public class ShowFullImage extends CrashActivity {

    private ViewPager mSliderViewPager;
    private TextView mDotLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_full_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.
                    FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.
                    TRANSPARENT);
        }
        Common.setBack(this);

        mDotLayout = findViewById(R.id.linearLayout);
        mSliderViewPager = findViewById(R.id.slideViewPage);

        FullImageAdapter cabImageAdapter = new FullImageAdapter(ShowFullImage.this, Common.cabImg);
        mSliderViewPager.setAdapter(cabImageAdapter);
        addDotsIndicator(0);
        mSliderViewPager.addOnPageChangeListener(viewListener);
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void addDotsIndicator(int position) {
        mDotLayout.setVisibility(View.VISIBLE);
        mDotLayout.setText("" + (position + 1) + "/" + Common.cabImg.size());
    }


}
