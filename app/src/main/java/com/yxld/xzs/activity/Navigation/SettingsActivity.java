package com.yxld.xzs.activity.Navigation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.yxld.xzs.R;
import com.yxld.xzs.base.BaseActivity;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hugeterry.updatefun.UpdateFunGO;
/**
 * Created by yishangfei on 2016/10/9 0009.
 * 欣助手应用设置
 */

public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.setting_place)
    TextView settingPlace;
    @BindView(R.id.setting_customer)
    TextView settingCustomer;
    @BindView(R.id.version_number)
    TextView versionNumber;
    @BindView(R.id.switch_ring)
    Switch switchRing;
    @BindView(R.id.switch_shock)
    Switch switchShock;
    @BindView(R.id.setting_update)
    AutoRelativeLayout settingUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initview();
    }

    private void initview() {
        versionNumber.setText("v" + getAppInfo());
        switchRing.setOnCheckedChangeListener(this);
        switchShock.setOnCheckedChangeListener(this);

        SharedPreferences sp = getSharedPreferences("ls", MODE_PRIVATE);
        boolean ring = sp.getBoolean("rememberPass", false);
        switchRing.setChecked(ring);
        SharedPreferences sp1 = getSharedPreferences("zd", MODE_PRIVATE);
        boolean shock = sp1.getBoolean("rememberPass", false);
        switchShock.setChecked(shock);
    }

    @OnClick({R.id.setting_place, R.id.setting_customer, R.id.setting_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_place://我的位置

                break;
            case R.id.setting_customer://联系客服
                break;
            case R.id.setting_update://检查更新
                UpdateFunGO.manualStart(this);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_ring:
                SharedPreferences sp = getSharedPreferences("ls", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if (isChecked) {
                    editor.putBoolean("rememberPass", true);
                } else {
                    editor.remove("rememberPass");
                }
                editor.apply();
                break;
            case R.id.switch_shock:
                SharedPreferences sp1 = getSharedPreferences("zd", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sp1.edit();
                if (isChecked) {
                    editor1.putBoolean("rememberPass", true);
                } else {
                    editor1.remove("rememberPass");
                }
                editor1.apply();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }

    private String getAppInfo() {
        try {
            String pkName = this.getPackageName();
            String versionName = this.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
        }
        return null;
    }
}
