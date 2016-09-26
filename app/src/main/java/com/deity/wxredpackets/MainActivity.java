package com.deity.wxredpackets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import com.deity.wxredpackets.adapter.RedPacketAdapter;
import com.deity.wxredpackets.dao.WXRedPacketDaoImpl;
import com.deity.wxredpackets.data.WXRedPacketApplication;
import com.deity.wxredpackets.presenter.WXRedPacketPresenter;
import com.deity.wxredpackets.view.IWXRedPacketView;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener,IWXRedPacketView,AccessibilityManager.AccessibilityStateChangeListener {//,NavigationView.OnNavigationItemSelectedListener

    //AccessibilityService 管理
    private AccessibilityManager accessibilityManager;
    private WXRedPacketPresenter packetPresenter;
//    @BindView(R.id.btn_control) public Button btn_control;/**没网络，gradle下载不了*/
    public Button btn_control;
    public Button btn_setting;
    public Button btn_clear;
    public Button btn_share;
    public TextView already_get;
    private RedPacketAdapter mRedPacketAdapter;
    public RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initAccessibility();
        ButterKnife.bind(this);
        initViews();

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
    }

    public void initViews(){
        packetPresenter = new WXRedPacketPresenter(this);
        btn_control = (Button) this.findViewById(R.id.btn_control);
        btn_setting = (Button) this.findViewById(R.id.btn_setting);
        btn_clear = (Button) this.findViewById(R.id.btn_clear);
        btn_share = (Button) this.findViewById(R.id.btn_share);
        already_get = (TextView) this.findViewById(R.id.already_get);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("警告")
                        .setMessage("该操作将清空抢红包记录，是否继续?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                WXRedPacketDaoImpl.getInstance().deleteWXRedPacketTx();
                                updateRecord();
                            }
                        })
                        .setNegativeButton("取消",null).show();

            }
        });
        btn_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccessibility();
            }
        });
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettings();
            }
        });
        initRecycleView();
    }

    public void initRecycleView(){
        mSwipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.widget_refresh);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mRedPacketAdapter = new RedPacketAdapter(MainActivity.this);
        mRedPacketAdapter.setData(WXRedPacketDaoImpl.getInstance().queryWXRedPacket());

        /**线性布局*/
        mRecyclerView = (RecyclerView) this.findViewById(R.id.app_record);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setAdapter(mRedPacketAdapter);
    }

    public void initAccessibility(){
        //监听AccessibilityService 变化
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
    }

    public void destoryAccessibility(){
        accessibilityManager.removeAccessibilityStateChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryAccessibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
    @SuppressWarnings("unused")
    @OnClick(R.id.btn_control)
    private void openAccessibility() {
        Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(accessibleIntent);
    }
    @SuppressWarnings("unused")
    @OnClick(R.id.btn_setting)
    private void openSettings() {
        Intent settingsIntent = new Intent(this, SettingActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        packetPresenter.updateServiceState(accessibilityManager);//onAccessibilityStateChanged 实际测试中并没办法实时监测
        updateRecord();
    }

    @Override
    public void onAccessibilityStateChanged(boolean b) {
        packetPresenter.updateServiceState(accessibilityManager);
    }

    @Override
    public void openService() {
        btn_control.setText("开启插件");

    }

    @Override
    public void closeService() {
        btn_control.setText("关闭插件");
    }

    public void updateRecord(){
        mRedPacketAdapter.setData(WXRedPacketDaoImpl.getInstance().queryWXRedPacket());
        mRedPacketAdapter.notifyDataSetChanged();
        Log.e(TAG,"已为您抢到了"+WXRedPacketDaoImpl.getInstance().queryTotalMoney()+"元");
        already_get.setText(String.format(WXRedPacketApplication.instance.getResources().getString(R.string.already_get),WXRedPacketDaoImpl.getInstance().queryTotalMoney()));
    }

    @Override
    public void onRefresh() {
        updateRecord();
        mSwipeLayout.setRefreshing(false);
    }

    public void shareApp(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"告诉好友有个好应用");
        intent.putExtra(Intent.EXTRA_TEXT,"抢红包怎么能没有一个好工具呢,快试试在应用商店搜索[微信红包助手]吧");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent,getTitle()));

    }
}
