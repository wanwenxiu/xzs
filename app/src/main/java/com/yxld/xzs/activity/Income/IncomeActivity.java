package com.yxld.xzs.activity.Income;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yxld.xzs.R;
import com.yxld.xzs.base.BaseActivity;
import com.yxld.xzs.contain.Contains;
import com.yxld.xzs.http.ServiceFactory;
import com.yxld.xzs.http.exception.ApiException;
import com.yxld.xzs.subscribers.RxSubscriber;
import com.yxld.xzs.transformer.DefaultTransformer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yishangfei on 2016/10/11 0011.
 * 我的收入
 */

public class IncomeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.income_assets)
    TextView mIncomeAssets;
    @BindView(R.id.income_money)
    TextView mIncomeMoney;
    @BindView(R.id.income_button)
    Button mIncomeButton;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout incomeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        incomeRefresh.setOnRefreshListener(this);
        incomeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        Income();
    }

    //网络请求
    private void Income() {
        ServiceFactory.httpService()
                .total(Contains.appLogin.getAdminId())
                .compose(new DefaultTransformer<String>())
                .subscribe(new RxSubscriber<String>(this) {
                    @Override
                    public void onNext(String total) {
                        if (incomeRefresh != null) {
                            incomeRefresh.setRefreshing(false);
                        }
                        mIncomeMoney.setText(total);
                    }

                    @Override
                    protected void onError(ApiException ex) {
                        super.onError(ex);
                        if (incomeRefresh != null) {
                            incomeRefresh.setRefreshing(false);
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.income, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.details:
                Intent details = new Intent(this, DetailsActivity.class);
                startActivity(details);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.income_assets, R.id.income_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.income_assets:
                 /*初始化dialog构造器*/
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");/*设置dialog的title*/
                builder.setMessage("总资产:账户的余额和积分总和");/*设置dialog的内容*/
                builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {/*设置dialog确认按钮的点击事件*/
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(IncomeActivity.this, "你知道了呦~~~", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setCancelable(false);/*设置是否可以通过返回键消失, 默认true*/
                builder.show();/*显示dialog*/
                break;
            case R.id.income_button:
                Toast.makeText(this, "暂时无法提现，功能有待实现", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onRefresh() {
        Income();
    }
}
