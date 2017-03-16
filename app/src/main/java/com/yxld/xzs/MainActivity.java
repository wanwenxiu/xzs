package com.yxld.xzs;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.yxld.xzs.activity.Income.IncomeActivity;
import com.yxld.xzs.activity.Login.LoginActivity;
import com.yxld.xzs.activity.Main.GrabFragment;
import com.yxld.xzs.activity.Main.PickFragment;
import com.yxld.xzs.activity.Main.SendFragment;
import com.yxld.xzs.activity.Navigation.NightActivity;
import com.yxld.xzs.activity.Navigation.QuardActivity;
import com.yxld.xzs.activity.Navigation.SettingsActivity;
import com.yxld.xzs.activity.Navigation.SummaryActivity;
import com.yxld.xzs.activity.Repair.RepairActivity;
import com.yxld.xzs.base.DemoApplicationLike;
import com.yxld.xzs.contain.Contains;
import com.yxld.xzs.http.ServiceFactory;
import com.yxld.xzs.http.exception.ApiException;
import com.yxld.xzs.subscribers.RxSubscriber;
import com.yxld.xzs.transformer.DefaultTransformer;
import com.yxld.xzs.view.TabLayoutPlus;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.ielse.view.SwitchView;
import cn.hugeterry.updatefun.UpdateFunGO;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwitchView.OnStateChangedListener {

    @BindView(R.id.exit)
    Button exit;
    @BindView(R.id.settings)
    Button settings;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ImageView load_push;
    private SwitchView switchButton;
    private View headerLayout, nav_header_view;
    private TextView nav_header_text, nav_header_name, nav_header_fuzeren;
    public static ViewPager viewPager;
    public static TabLayoutPlus tabLayout;
    private TabLayout.Tab Grab, Pick, Service;

    private long exitTime = 0;
    public static List<String> logList = new CopyOnWriteArrayList<String>();
    private String[] mTitles = new String[]{"待抢单", "待取货", "待送达"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setSupportActionBar(toolbar);
        StatusBarUtil.setColorForDrawerLayout(this, (DrawerLayout) findViewById(R.id.drawer_layout), ContextCompat.getColor(this, R.color.toolbar_color), 112);
        //极光推送
        String topic = Contains.appLogin.getAdminNickName();
        JPushInterface.setAlias(this,topic, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.d("geek", "JPushInterface setAlias  gotResult: "+i);
            }
        });
        //fir更新下载
        UpdateFunGO.init(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        initview();
        initTinker();

    }

    //微信Tinker热修复
    private void initTinker() {
        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed.apk");
    }

    private void initview() {
        tabLayout = (TabLayoutPlus) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        nav_header_view = headerLayout.findViewById(R.id.nav_header_view);
        switchButton = (SwitchView) headerLayout.findViewById(R.id.SwitchButton);
        load_push = (ImageView) headerLayout.findViewById(R.id.load_push);
        nav_header_text = (TextView) headerLayout.findViewById(R.id.nav_header_text);
        nav_header_name = (TextView) headerLayout.findViewById(R.id.nav_header_name);
        nav_header_fuzeren = (TextView) headerLayout.findViewById(R.id.nav_header_fuzeren);
        switchButton.setOnStateChangedListener(this);
        nav_header_name.setText(Contains.appLogin.getAdminNickName());
        if (Contains.appLogin.getCxwyPeisongFuzeren() != null && Contains.appLogin.getCxwyPeisongFuzeren() == 0) {
            nav_header_fuzeren.setText("值班负责人");
        } else {
            nav_header_fuzeren.setText("");
        }

        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        Grab = tabLayout.getTabAt(0);
        Pick = tabLayout.getTabAt(1);
        Service = tabLayout.getTabAt(2);
        viewPager.setOffscreenPageLimit(2);
    }

    FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                return new PickFragment();
            } else if (position == 2) {
                return new SendFragment();
            }
            return new GrabFragment();
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

    };

    @OnClick({R.id.settings, R.id.exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings:
                startActivity(SettingsActivity.class);
                break;
            case R.id.exit:
                //注销极光推送
                JPushInterface.setAlias(this,"", new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        Log.d("geek", "JPushInterface setAlias  gotResult: "+i);
                    }
                });
                //清除SharedPreferences里的账号数据
                SharedPreferences sp = this.getSharedPreferences("userInfo", 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("NAME", "");
                editor.putString("PASSWORD", "");
                editor.putBoolean("ISCHECK", false);
                editor.putBoolean("AUTO_ISCHECK", false);
                editor.commit();
                Contains.appLogin = null;
                logList.clear();
                finish();
                Intent mine = new Intent(this, LoginActivity.class);
                startActivity(mine);
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_income) {
            //我的收入
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(300);
                        startActivity(IncomeActivity.class);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } else if (id == R.id.nav_summary) {
            //工作汇总
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(300);
                        startActivity(SummaryActivity.class);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else if (id == R.id.nav_invitation) {
            //邀请好友
            Toast.makeText(MainActivity.this, "尽快更新中", Toast.LENGTH_SHORT).show();
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    try {
//                        sleep(300);
//                        //    startActivity(InvitationActivity.class);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
        } else if (id == R.id.nav_customer) {
            //联系客服
            Hotline();
        } else if (id == R.id.nav_night) {
            //通知管理
            if (Contains.appLogin.getCxwyPeisongFuzeren() == 0) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(300);
                            startActivity(NightActivity.class);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } else {
                Toast.makeText(MainActivity.this, "您暂时没有权限无法进入", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_quard) {
            //特殊门禁
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(300);
                        startActivity(QuardActivity.class);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else if (id == R.id.nav_approval) {
            //报修审批
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(300);
                        startActivity(RepairActivity.class);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    //获取客服电话网络请求
    private void Hotline() {
        ServiceFactory.httpService()
                .hotline(Contains.appLogin.getAdminXiangmuId().toString())
                .compose(new DefaultTransformer<String>())
                .subscribe(new RxSubscriber<String>(MainActivity.this) {
                    @Override
                    public void onNext(final String hotline) {
                        Logger.d(hotline);
                        RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
                        rxPermissions.setLogging(true);
                        rxPermissions.requestEach(Manifest.permission.CALL_PHONE)
                                .subscribe(new Action1<Permission>() {
                                    @Override
                                    public void call(Permission permission) {
                                        Logger.d("Permission result " + permission);
                                        if (permission.granted) {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_CALL);
                                            //url:统一资源定位符
                                            //uri:统一资源标示符（更广）
                                            intent.setData(Uri.parse("tel:" + hotline));
                                            //开启系统拨号器
                                            startActivity(intent);
                                        } else if (permission.shouldShowRequestPermissionRationale) {
                                            // Denied permission without ask never again
                                            Toast.makeText(MainActivity.this,
                                                    "没有访问也没有拒绝",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Denied permission with ask never again
                                            // Need to go to the settings
                                            Toast.makeText(MainActivity.this,
                                                    "没有权限,您不能打电话",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
    }

    //后退两次回到桌面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次后台运行",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断开关状态
        if (Contains.appLogin.getCxwyPeisongState().equals("开启")) {
            headerLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.drawer_blue));
            nav_header_view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.drawer_light_blue));
            nav_header_text.setText("接单中");
            switchButton.setOpened(true);
        } else {
            headerLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.drawer_gray));
            nav_header_view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.drawer_light_gray));
            nav_header_text.setText("未开工");
            switchButton.setOpened(false);
        }
    }


    /**
     * 跳转到指定的Activity
     *
     * @param targetActivity 要跳转的目标Activity
     */
    private void startActivity(@NonNull Class<?> targetActivity) {
        startActivity(new Intent(this, targetActivity));
    }

    //状态开关
    @Override
    public void toggleToOn(SwitchView view) {
        load_push.setVisibility(View.VISIBLE);
        ((AnimationDrawable) load_push.getBackground()).start();
        ServiceFactory.httpService()
                .state(Contains.appLogin.getAdminId(), "开启")
                .compose(new DefaultTransformer<String>())
                .subscribe(new RxSubscriber<String>(MainActivity.this) {
                    @Override
                    public void onNext(String state) {
                        ((AnimationDrawable) load_push.getBackground()).stop();
                        load_push.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "接单开启成功", Toast.LENGTH_SHORT).show();
                        switchButton.toggleSwitch(true);
                        headerLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.drawer_blue));
                        nav_header_view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.drawer_light_blue));
                        nav_header_text.setText("接单中");
                    }

                    @Override
                    protected void onError(ApiException ex) {
                        super.onError(ex);
                        ((AnimationDrawable) load_push.getBackground()).stop();
                        load_push.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "接单开启失败，请稍后重新尝试", Toast.LENGTH_SHORT).show();
                        switchButton.toggleSwitch(false);
                    }
                });
    }

    @Override
    public void toggleToOff(SwitchView view) {
        load_push.setVisibility(View.VISIBLE);
        ((AnimationDrawable) load_push.getBackground()).start();
        ServiceFactory.httpService()
                .state(Contains.appLogin.getAdminId(), "关闭")
                .compose(new DefaultTransformer<String>())
                .subscribe(new RxSubscriber<String>(MainActivity.this) {
                    @Override
                    public void onNext(String state) {
                        ((AnimationDrawable) load_push.getBackground()).stop();
                        load_push.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "接单关闭成功", Toast.LENGTH_SHORT).show();
                        switchButton.toggleSwitch(false);
                        headerLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.drawer_gray));
                        nav_header_view.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.drawer_light_gray));
                        nav_header_text.setText("未开工");
                    }

                    @Override
                    protected void onError(ApiException ex) {
                        super.onError(ex);
                        ((AnimationDrawable) load_push.getBackground()).stop();
                        load_push.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "接单关闭失败，请稍后重新尝试", Toast.LENGTH_SHORT).show();
                        switchButton.toggleSwitch(true);
                    }
                });
    }
}
