package com.deity.wxredpackets;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
    public Button btn_avoid;
    public Button btn_share;
    public TextView already_get;
    private RedPacketAdapter mRedPacketAdapter;
    public RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private final String TAG = MainActivity.class.getSimpleName();
    private static final int NOTIFICATION_FLAG = 1;
    private NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initAccessibility();
        ButterKnife.bind(this);
        initViews();
        // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public void initViews(){
        packetPresenter = new WXRedPacketPresenter(this);
        btn_control = (Button) this.findViewById(R.id.btn_control);
        btn_setting = (Button) this.findViewById(R.id.btn_setting);
        btn_avoid = (Button) this.findViewById(R.id.btn_avoid);
        btn_share = (Button) this.findViewById(R.id.btn_share);
        already_get = (TextView) this.findViewById(R.id.already_get);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });
        btn_avoid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avoidClose();
            }
        });
        btn_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialog_help = getLayoutInflater().inflate(R.layout.dialog_help,null);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("提示信息")
                        .setMessage(WXRedPacketApplication.instance.getString(R.string.open_function))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                openAccessibility();
                            }
                        })
                        .setNegativeButton("取消",null).show();
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

    public void actionClear(){
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

    public void avoidClose(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("警告信息")
                .setMessage(WXRedPacketApplication.instance.getString(R.string.tips_avoid_close))
                .setPositiveButton("确定", null)
                .setNegativeButton("取消",null).show();
    }

    /**
     * 免责声明
     */
    public void actionSay(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("警告信息")
                .setMessage(WXRedPacketApplication.instance.getString(R.string.tips_mianze))
                .setPositiveButton("确定", null)
                .setNegativeButton("取消",null).show();
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

    @Override
    public void clearNotification(){
        // 清除id为NOTIFICATION_FLAG的通知
        manager.cancel(NOTIFICATION_FLAG);
    }

    @Override
    public void createNotification(){
        PendingIntent pendingIntent3 = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API16之后才支持
        Notification serviceRunNormal = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(WXRedPacketApplication.instance.getResources().getString(R.string.app_name)+"正在为您服务!")
                .setContentTitle(WXRedPacketApplication.instance.getResources().getString(R.string.app_name)+"正在为您服务!")
                .setContentText("如果看到这条消息,代表服务正常运行!")
                .setContentIntent(pendingIntent3).setNumber(1).build(); // 需要注意build()是在API
        // level16及之后增加的，API11可以使用getNotificatin()来替代
        serviceRunNormal.flags |= Notification.FLAG_NO_CLEAR; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        manager.notify(NOTIFICATION_FLAG, serviceRunNormal);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
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
//        if (id == R.id.action_settings) {
//            openSettings();
//            return true;
//        }
        switch (id){
            case R.id.action_setting:
                openSettings();
                return true;
            case R.id.action_clear:
                actionClear();
                return true;
            case R.id.action_say:
                actionSay();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
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
        btn_control.setTextColor(getResources().getColor(R.color.colorAccent));

    }

    @Override
    public void closeService() {
        btn_control.setText("关闭插件");
        btn_control.setTextColor(getResources().getColor(R.color.colorGreen));
    }

    public void updateRecord(){
        mRedPacketAdapter.setData(WXRedPacketDaoImpl.getInstance().queryWXRedPacket());
        mRedPacketAdapter.notifyDataSetChanged();
        double alreadyGetMoney = WXRedPacketDaoImpl.getInstance().queryTotalMoney();
        Log.e(TAG,"已为您抢到了"+alreadyGetMoney+"元");
        if (0.0==alreadyGetMoney){
            already_get.setText(WXRedPacketApplication.instance.getText(R.string.get_i_cant));
        }else {
            already_get.setText(String.format(WXRedPacketApplication.instance.getResources().getString(R.string.already_get), alreadyGetMoney));
        }
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
        intent.putExtra(Intent.EXTRA_TEXT,WXRedPacketApplication.instance.getString(R.string.app_share));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent,getTitle()));

    }
}
