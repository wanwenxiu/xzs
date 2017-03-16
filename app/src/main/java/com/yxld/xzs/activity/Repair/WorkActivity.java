package com.yxld.xzs.activity.Repair;
////////////////////////////////////////////////////////////////////
//                          _ooOoo_                               //
//                         o8888888o                              //
//                         88" . "88                              //
//                         (| ^_^ |)                              //
//                         O\  =  /O                              //
//                      ____/`---'\____                           //
//                    .'  \\|     |//  `.                         //
//                   /  \\|||  :  |||//  \                        //
//                  /  _||||| -:- |||||-  \                       //
//                  |   | \\\  -  /// |   |                       //
//                  | \_|  ''\---/''  |   |                       //
//                  \  .-\__  `-`  ___/-. /                       //
//                ___`. .'  /--.--\  `. . ___                     //
//              ."" '<  `.___\_<|>_/___.'  >'"".                  //
//            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
//            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
//      ========`-.____`-.___\_____/___.-`____.-'========         //
//                           `=---='                              //
//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
//         佛祖保佑       永无BUG     永不修改                  //
////////////////////////////////////////////////////////////////////

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.yxld.xzs.R;
import com.yxld.xzs.base.BaseActivity;
import com.yxld.xzs.contain.Contains;
import com.yxld.xzs.entity.AppAssign;
import com.yxld.xzs.entity.AppRepair;
import com.yxld.xzs.http.ServiceFactory;
import com.yxld.xzs.http.exception.ApiException;
import com.yxld.xzs.subscribers.RxSubscriber;
import com.yxld.xzs.transformer.DefaultTransformer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yishangfei on 2017/3/22 0022.
 * 个人主页：http://yishangfei.me
 * Github:https://github.com/yishangfei
 *
 * 主管指派
 */
public class WorkActivity extends BaseActivity {
    @BindView(R.id.work_single)
    TextView workSingle;
    @BindView(R.id.work_time)
    TextView workTime;
    @BindView(R.id.work_address)
    TextView workAddress;
    @BindView(R.id.work_name)
    TextView workName;
    @BindView(R.id.work_tel)
    TextView workTel;
    @BindView(R.id.work_details)
    TextView workDetails;
    @BindView(R.id.work_select)
    TextView workSelect;
    @BindView(R.id.work_image1)
    ImageView workImage1;
    @BindView(R.id.work_image2)
    ImageView workImage2;
    @BindView(R.id.work_image3)
    ImageView workImage3;
    @BindView(R.id.work_button)
    Button workButton;

    private AppRepair appRepair;
    private int adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        appRepair = intent.getParcelableExtra("Parcelable");
        workSingle.setText(appRepair.getBaoxiu_danhao());
        workTime.setText(appRepair.getBaoxiu_lrtime());
        //获取地址
        String address = appRepair.getBaoxiu_loupan() + appRepair.getBaoxiu_loudong() + "栋" + appRepair.getBaoxiu_danyuan() + "单元" + appRepair.getBaoxiu_fanghao() + "　" + appRepair.getBaoxiu_didian();
        workAddress.setText(address);
        workName.setText(appRepair.getBaoxiu_name());
        workTel.setText(appRepair.getBaoxiu_dianhua());
        workDetails.setText(appRepair.getBaoxiu_xiangmu());
        //设置图片
        Logger.d(appRepair.getBaoxiu_img());
        String images[] = appRepair.getBaoxiu_img().split("\\;");
        for (int i = 0; i < images.length; i++) {
            switch (i){
                case 0:
                    Glide.with(this)
                            .load("http://img0.hnchxwl.com/" + images[0])
                            .crossFade()
                            .into(workImage1);
                    break;
                case 1:
                    Glide.with(this)
                            .load("http://img0.hnchxwl.com/" + images[1])
                            .crossFade()
                            .into(workImage2);
                    break;
                case 2:
                    Glide.with(this)
                            .load("http://img0.hnchxwl.com/" + images[2])
                            .crossFade()
                            .into(workImage3);
                    break;
            }

        }
    }

    @OnClick({R.id.work_select, R.id.work_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.work_select:
                Assign();
                break;
            case R.id.work_button:
                if(workSelect.getText().equals("")){
                    Toast.makeText(this, "请选择需要指派的维修员", Toast.LENGTH_SHORT).show();
                }else {
                ServiceFactory.httpService()
                        .point(Contains.appLogin.getAdminId(), adminId, appRepair.getBaoxiu_id())
                        .compose(new DefaultTransformer<String>())
                        .subscribe(new RxSubscriber<String>(this) {
                            @Override
                            public void onNext(String s) {
                                Toast.makeText(WorkActivity.this, "指派成功", Toast.LENGTH_SHORT).show();
                                finish();
                                System.gc();
                            }
                        });
                }
                break;
        }
    }


    //指派人获取
    private void Assign() {
        ServiceFactory.httpService()
                .assign(Contains.appLogin.getAdminId())
                .compose(new DefaultTransformer<List<AppAssign>>())
                .subscribe(new RxSubscriber<List<AppAssign>>(this) {
                    @Override
                    public void onNext(List<AppAssign> appAssigns) {
                        final int[] id = new int[appAssigns.size()];
                        final String[] name = new String[appAssigns.size()];
                        for (int i = 0; i < appAssigns.size(); i++) {
                            name[i] = appAssigns.get(i).getAdminNickName();
                            id[i] = appAssigns.get(i).getAdminId();
                        }
                        new AlertView("选择维修工人", null, "取消", null,
                                name,
                                WorkActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == -1) {

                                } else {
                                    workSelect.setText(name[position]);
                                    adminId = id[position];
                                }
                            }
                        }).show();
                    }

                    @Override
                    protected void onError(ApiException ex) {
                        super.onError(ex);
                    }
                });
    }
}
