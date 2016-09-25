package com.deity.wxredpackets;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.deity.wxredpackets.fragment.GeneralSettingsFragment;

/**
 * 设置界面
 * TODO 鉴于微信在防红包辅助的积极行动，需提供手动更新的入口地址,等待完善
 * Created by Deity on 2016/9/20.
 */
public class SettingActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        initViews();
        initToolBar();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initToolBar() {
        mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setTitle("偏好设置");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingActivity.this.finish();
            }
        });

    }

    public void initViews() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.preference_content, new GeneralSettingsFragment());
        transaction.commit();

    }

}
