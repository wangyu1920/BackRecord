package com.example.backrecord.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.backrecord.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager2+Fragment实现微信滑动界面
 * ViewPager2相比ViewPager，拥有懒加载功能
 *
 * 思路：
 *      1、底部四个view，带有选中后颜色变色的效果，这个我使用了自定义的View,
 *          正常情况可以使用一个ImageView，设定其Resource为R.drawable.selector.xml,
 *          使用imageView.setSelected(true/false)自动改变其图片资源。
 *          为其设置监听器，点击后pager2.setCurrentItem()
 *      2、上面的ViewPager2设置Adapter，extends FragmentStateAdapter；
 *          重写构造方法，传入List<BlankFragment> fragments
 *          设置pager2.registerOnPageChangeCallback(),使得滑动页面时下面的view也变
 *      3、BlankFragment,构造方法传入参数，以此参数为其布局中的textView设置text
 */
public class LauncherActivity extends AppCompatActivity implements View.OnClickListener{

    TextView bv1,bv2,bv3;
    ViewPager2 pager2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanucher);
        getView();
        initViewPager2();
        registerListener();
    }
    private void registerListener() {
        bv1.setOnClickListener(this);
        bv2.setOnClickListener(this);
        bv3.setOnClickListener(this);
    }

    private void initViewPager2() {
        List<BlankFragment> fragments = new ArrayList<>();
        fragments.add(BlankFragment.newInstance(BlankFragment.FLAG_SETTING));
        fragments.add(BlankFragment.newInstance(BlankFragment.FLAG_MOVIES));
        fragments.add(BlankFragment.newInstance(BlankFragment.FLAG_HELP));
        pager2.setAdapter(new ViewPager2Adapter(getSupportFragmentManager(), getLifecycle(), fragments));
        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectBottomViews(position);
            }
        });
    }

    private void selectBottomViews(int position) {

        bv1.setSelected(false);
        bv2.setSelected(false);
        bv3.setSelected(false);

        switch (position) {
            case 0:
                bv1.setSelected(true);
                break;
            case 1:
                bv2.setSelected(true);
                break;
            case 2:
                bv3.setSelected(true);
                break;
        }
        bv1.invalidate();
        bv2.invalidate();
        bv3.invalidate();
    }
    private void getView() {
        bv1 = findViewById(R.id.bv1);
        bv2 = findViewById(R.id.bv2);
        bv3 = findViewById(R.id.bv3);
        pager2 = findViewById(R.id.pager);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bv1:
                selectBottomViews(0);
                pager2.setCurrentItem(0,true);
                break;
            case R.id.bv2:
                selectBottomViews(1);
                pager2.setCurrentItem(1,true);
                break;
            case R.id.bv3:
                selectBottomViews(2);
                pager2.setCurrentItem(2,true);
                break;
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
