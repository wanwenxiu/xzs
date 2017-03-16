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

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.orhanobut.logger.Logger;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.qiniu.android.utils.UrlSafeBase64;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yxld.xzs.R;
import com.yxld.xzs.adapter.PhotoAdapter;
import com.yxld.xzs.contain.Contains;
import com.yxld.xzs.entity.AppRepair;
import com.yxld.xzs.http.ServiceFactory;
import com.yxld.xzs.subscribers.RxSubscriber;
import com.yxld.xzs.transformer.DefaultTransformer;
import com.yxld.xzs.transformer.DefaultTransformer1;
import com.yxld.xzs.utils.AudioService;
import com.yxld.xzs.utils.MediaManager;
import com.yxld.xzs.utils.StringUitls;
import com.yxld.xzs.view.AudioRecordButton;
import com.yxld.xzs.view.AudioViewPage;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by yishangfei on 2017/3/23 0023.
 * 个人主页：http://yishangfei.me
 * Github:https://github.com/yishangfei
 * <p>
 * 维修详情
 */
public class TreatFragment extends TakePhotoFragment {
    @BindView(R.id.treat_name)
    TextView treatName;
    @BindView(R.id.treat_time)
    TextView treatTime;
    @BindView(R.id.treat_tel)
    TextView treatTel;
    @BindView(R.id.treat_image1)
    ImageView treatImage1;
    @BindView(R.id.treat_image2)
    ImageView treatImage2;
    @BindView(R.id.treat_image3)
    ImageView treatImage3;
    @BindView(R.id.treat_image4)
    ImageView treatImage4;
    @BindView(R.id.treat_image5)
    ImageView treatImage5;
    @BindView(R.id.treat_image6)
    ImageView treatImage6;
    @BindView(R.id.treat_recycler1)
    RecyclerView treatRecycler1;
    @BindView(R.id.treat_recycler2)
    RecyclerView treatRecycler2;
    @BindView(R.id.treat_audio)
    AudioRecordButton treatAudio;
    @BindView(R.id.treat_details)
    EditText treatDetails;
    @BindView(R.id.treat_button)
    Button treatButton;
    @BindView(R.id.radioButton)
    RadioButton radioButton;
    @BindView(R.id.radioButton2)
    RadioButton radioButton2;
    @BindView(R.id.treat_voice)
    AutoLinearLayout treatVoice;
    @BindView(R.id.treat_record)
    AutoLinearLayout treatRecord;

    private int mark, ImgIndex;
    private String before_key = "";
    private String back_key = "";
    private String file = "";
    private String details = "";
    private AppRepair appRepair;
    private PhotoAdapter photoAdapter;
    private PhotoAdapter photoAdapter1;
    private UploadManager uploadManager;
    private volatile boolean isCancelled = false;
    private List<TImage> selectMedia = new ArrayList<>();
    private List<TImage> selectMedia1 = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_treat, null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this, view);
        Intent intent = getActivity().getIntent();
        appRepair = intent.getParcelableExtra("Parcelable");
        initView();
        initRadio();
        return view;
    }

    private void initView() {
        //主管进来 进行审批状态等于5
        if (appRepair.getBaoxiu_status().equals("5")) {
            treatImage1.setVisibility(View.VISIBLE);
            treatImage2.setVisibility(View.VISIBLE);
            treatImage3.setVisibility(View.VISIBLE);
            treatImage4.setVisibility(View.VISIBLE);
            treatImage5.setVisibility(View.VISIBLE);
            treatImage6.setVisibility(View.VISIBLE);
            treatVoice.setVisibility(View.INVISIBLE);
            treatRecycler1.setVisibility(View.GONE);
            treatRecycler2.setVisibility(View.GONE);
            treatAudio.setText("文字留言");
            treatAudio.setEnabled(false);
            treatDetails.setHint("请输入您的文字留言");
            Drawable weather1 = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_voice_write1);
            weather1.setBounds(0, 0, weather1.getMinimumWidth(), weather1.getMinimumWidth());
            treatAudio.setCompoundDrawables(null, weather1, null, null);
            treatButton.setText("验收完成");
            String images[] = appRepair.getBaoxiu_chayanimg().split("\\~");
            if (images.length == 2) {
                String image1[] = images[0].split("\\;");
                String image2[] = images[1].split("\\;");
                Logger.d(image1);
                Logger.d(image2);
                for (int i = 0; i < image1.length; i++) {
                    switch (i) {
                        case 1:
                            Glide.with(this)
                                    .load("http://img0.hnchxwl.com/" + image1[1])
                                    .crossFade()
                                    .into(treatImage1);
                            break;
                        case 2:
                            Glide.with(this)
                                    .load("http://img0.hnchxwl.com/" + image1[2])
                                    .crossFade()
                                    .into(treatImage2);
                            break;
                        case 3:
                            Glide.with(this)
                                    .load("http://img0.hnchxwl.com/" + image1[3])
                                    .crossFade()
                                    .into(treatImage3);
                            break;
                    }
                }
                for (int j = 0; j < image2.length; j++) {
                    switch (j) {
                        case 0:
                            Glide.with(this)
                                    .load("http://img0.hnchxwl.com/" + image2[0])
                                    .crossFade()
                                    .into(treatImage4);
                            break;
                        case 1:
                            Glide.with(this)
                                    .load("http://img0.hnchxwl.com/" + image2[1])
                                    .crossFade()
                                    .into(treatImage5);
                            break;
                        case 2:
                            Glide.with(this)
                                    .load("http://img0.hnchxwl.com/" + image2[2])
                                    .crossFade()
                                    .into(treatImage6);
                            break;
                    }
                }
            }
            if (appRepair.getBaoxiu_fuzerenyijian() == null || appRepair.getBaoxiu_fuzerenyijian().equals("")) {
                treatDetails.setText("");
            } else {
                String yijian = appRepair.getBaoxiu_fuzerenyijian().replace("null", "");
                if (StringUitls.isChineseCharacter(yijian) == true) {
                    treatDetails.setText(yijian);
                } else {
                    treatDetails.setVisibility(View.GONE);
                    treatRecord.setVisibility(View.VISIBLE);
                    treatAudio.setText("语音留言");
                    Drawable weather = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_voice1);
                    weather.setBounds(0, 0, weather.getMinimumWidth(), weather.getMinimumWidth());
                    treatAudio.setCompoundDrawables(null, weather, null, null);
                }
            }
        } else {
            //维修员进来
            treatImage1.setVisibility(View.GONE);
            treatImage2.setVisibility(View.GONE);
            treatImage3.setVisibility(View.GONE);
            treatImage4.setVisibility(View.GONE);
            treatImage5.setVisibility(View.GONE);
            treatImage6.setVisibility(View.GONE);
            treatRecycler1.setVisibility(View.VISIBLE);
            treatRecycler2.setVisibility(View.VISIBLE);
            radioButton.setChecked(true);
            treatDetails.setEnabled(false);
            treatVoice.setVisibility(View.VISIBLE);
            //维修前上传图片的适配器
            treatRecycler1.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            photoAdapter = new PhotoAdapter(getActivity(), onAddPicClickListener);
            photoAdapter.setSelectMax(3);
            treatRecycler1.setAdapter(photoAdapter);
            //维修后上传图片的适配器
            treatRecycler2.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            photoAdapter1 = new PhotoAdapter(getActivity(), onAddPicClickListener1);
            photoAdapter1.setSelectMax(3);
            treatRecycler2.setAdapter(photoAdapter1);
            treatButton.setText("提交审核");
        }

        treatName.setText(appRepair.getBaoxiu_zx_weixiuren());
        treatTime.setText(appRepair.getBaoxiu_paidantime());
        treatTel.setText("");

    }


    //维修前加号的点击事件
    private PhotoAdapter.onAddPicClickListener onAddPicClickListener = new PhotoAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            mark = 0;
            switch (type) {
                case 0:
                    new AlertView("上传图片", null, "取消", null,
                            new String[]{"拍照", "从相册中选择"},
                            getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            TakePhoto takePhoto = getTakePhoto();
                            //获取TakePhoto图片路径
                            File file = new File(Environment.getExternalStorageDirectory(), "/xzs/" + System.currentTimeMillis() + ".jpg");
                            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                            Uri imageUri = Uri.fromFile(file);

                            configCompress(takePhoto);
                            configTakePhotoOption(takePhoto);
                            switch (position) {
                                case 0:
                                    takePhoto.onPickFromCapture(imageUri);
                                    break;
                                case 1:
                                    //设置最多几张
                                    takePhoto.onPickMultiple(3);
                                    break;
                            }
                        }
                    }).show();
                    break;
                case 1:
                    // 删除图片
                    selectMedia.remove(position);
                    photoAdapter.notifyItemRemoved(position);
                    break;
            }
        }
    };


    private PhotoAdapter.onAddPicClickListener onAddPicClickListener1 = new PhotoAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            mark = 1;
            switch (type) {
                case 0:
                    new AlertView("上传图片", null, "取消", null,
                            new String[]{"拍照", "从相册中选择"},
                            getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            TakePhoto takePhoto = getTakePhoto();
                            //获取TakePhoto图片路径
                            File file = new File(Environment.getExternalStorageDirectory(), "/xzs/" + System.currentTimeMillis() + ".jpg");
                            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                            Uri imageUri = Uri.fromFile(file);

                            configCompress(takePhoto);
                            configTakePhotoOption(takePhoto);
                            switch (position) {
                                case 0:
                                    takePhoto.onPickFromCapture(imageUri);
                                    break;
                                case 1:
                                    //设置最多几张
                                    takePhoto.onPickMultiple(3);
                                    break;
                            }
                        }
                    }).show();
                    break;
                case 1:
                    // 删除图片
                    selectMedia1.remove(position);
                    photoAdapter1.notifyItemRemoved(position);
                    break;
            }
        }
    };


    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }


    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showImg(result.getImages());
    }

    //图片成功后返回执行的方法
    private void showImg(ArrayList<TImage> images) {
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getCompressPath() != null) {
                if (mark == 0) {
                    selectMedia.add(images.get(i));
                } else {
                    selectMedia1.add(images.get(i));
                }
            }
        }
        if (selectMedia != null) {
            if (mark == 0) {
                photoAdapter.setList(selectMedia);
                photoAdapter.notifyDataSetChanged();
            } else {
                photoAdapter1.setList(selectMedia1);
                photoAdapter1.notifyDataSetChanged();
            }

        }
    }


    //设置Takephoto 使用TakePhoto自带的相册   照片旋转角度纠正
    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    //设置takephoto的照片使用压缩
    private void configCompress(TakePhoto takePhoto) {
        CompressConfig config;
        config = new CompressConfig.Builder()
                .setMaxSize(102400)
                .setMaxPixel(800)
                .enableReserveRaw(true)
                .create();
        takePhoto.onEnableCompress(config, false);
    }

    private void repair() {
        if (before_key.equals("") || back_key.equals("")) {
            Toast.makeText(getActivity(), "请上传好维修前或维修后的图片", Toast.LENGTH_SHORT).show();
        } else {
            String key = before_key + "~" + back_key;
            Logger.d(key);
            Logger.d(details);
            ServiceFactory.httpService()
                    .maintain(appRepair.getBaoxiu_id(), details, key)
                    .compose(new DefaultTransformer<String>())
                    .subscribe(new RxSubscriber<String>(getActivity()) {
                        @Override
                        public void onNext(String s) {
                            Toast.makeText(getActivity(), "维修成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            System.gc();
                        }
                    });
        }
    }

    private void initRadio() {
        treatAudio.setCanRecord(false);
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions
                .requestEach(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {
                            final AudioViewPage vPager =(AudioViewPage)getActivity().findViewById(R.id.viewPager);
                            vPager.setNoScroll(false);
                            treatAudio.setCanRecord(true);
                            treatAudio.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
                                @Override
                                public void onFinished(float seconds, String filePath) {
                                    Logger.d(seconds + "----------------");
                                    Logger.d(filePath + "===============");
                                    file = filePath;
                                    treatRecord.setVisibility(View.VISIBLE);
                                    treatDetails.setVisibility(View.GONE);
                                    vPager.setNoScroll(true);
                                }
                            });
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            Toast.makeText(getActivity(),
                                    "没有访问也没有拒绝",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            treatAudio.setCanRecord(false);
                            Toast.makeText(getActivity(),
                                    "没有权限,您不能录音,请进入设置打开权限。",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @OnClick({R.id.treat_button, R.id.treat_record, R.id.radioButton, R.id.radioButton2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.treat_record:
                if (appRepair.getBaoxiu_status().equals("5")) {
                    String yijian = appRepair.getBaoxiu_fuzerenyijian().replace("null", "");
                    Intent intent = new Intent(getActivity(), AudioService.class);
                    intent.putExtra("word", yijian);
                    getActivity().startService(intent);
                } else {
                    if (file.equals("")) {
                        Toast.makeText(getActivity(), "录音出现问题,请重新录音", Toast.LENGTH_SHORT).show();
                    } else {
                        //播放前重置。
                        MediaManager.release();
                        MediaManager.playSound(file);
                    }
                }
                break;
            case R.id.radioButton:
                radioButton.setChecked(true);
                radioButton2.setChecked(false);
                treatAudio.setEnabled(true);
                treatAudio.setText("语音留言");
                treatDetails.setText("");
                treatDetails.setEnabled(false);
                treatDetails.setHint("请您按住左边话筒录音");
                Drawable weather = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_voice1);
                weather.setBounds(0, 0, weather.getMinimumWidth(), weather.getMinimumWidth());
                treatAudio.setCompoundDrawables(null, weather, null, null);
                break;
            case R.id.radioButton2:
                treatRecord.setVisibility(View.GONE);
                treatDetails.setVisibility(View.VISIBLE);
                radioButton.setChecked(false);
                radioButton2.setChecked(true);
                treatAudio.setText("文字留言");
                treatAudio.setEnabled(false);
                treatDetails.setText("");
                treatDetails.setEnabled(true);
                treatDetails.setHint("请输入您的文字留言");
                Drawable weather1 = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_voice_write1);
                weather1.setBounds(0, 0, weather1.getMinimumWidth(), weather1.getMinimumWidth());
                treatAudio.setCompoundDrawables(null, weather1, null, null);
                break;
            case R.id.treat_button:
                if (treatButton.getText().equals("验收完成")) {
                    ServiceFactory.httpService()
                            .check(Contains.appLogin.getAdminId(), treatDetails.getText().toString(), null, appRepair.getBaoxiu_id())
                            .compose(new DefaultTransformer<String>())
                            .subscribe(new RxSubscriber<String>(getActivity()) {
                                @Override
                                public void onNext(String s) {
                                    Toast.makeText(getActivity(), "验收完成", Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                    System.gc();
                                }
                            });
                } else {
                    //维修完成请求接口
                    ServiceFactory.httpService()
                            .token()
                            .compose(new DefaultTransformer1<String>())
                            .subscribe(new RxSubscriber<String>(getActivity()) {
                                @Override
                                public void onNext(final String s) {
                                    if (radioButton.isChecked()) {
                                        //设定需要添加的自定义变量为Map<String, String>类型 并且放到UploadOptions第一个参数里面
                                        //上传策略中使用了自定义变量
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("Android", "报修");
                                        isCancelled = false;
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                                        String date = sdf.format(new Date());
                                        final String k = "upload/baoxiu/img/" + date + "/" + "android_" + System.currentTimeMillis() + "";
                                        //put第二个参数设置文件名
                                        uploadManager.put(file, k, s,
                                                new UpCompletionHandler() {
                                                    public void complete(String key,
                                                                         ResponseInfo info, JSONObject res) {
                                                        Logger.d(key + ",\r\n " + info
                                                                + ",\r\n " + res);
                                                        if (info.isOK() == true) {
                                                            Logger.d(res.toString());
                                                            details = k;
                                                            Logger.d(details + "============");
                                                        }
                                                    }
                                                }, new UploadOptions(map, null, false,
                                                        new UpProgressHandler() {
                                                            public void progress(String key, double percent) {
                                                                Log.i("qiniu", key + ": " + percent);
                                                            }

                                                        }, new UpCancellationSignal() {
                                                    @Override
                                                    public boolean isCancelled() {
                                                        return isCancelled;
                                                    }
                                                }));
                                    } else {
                                        details = treatDetails.getText().toString();
                                    }

                                    ImgIndex = 0;
                                    for (int i = 0; i < selectMedia.size(); i++) {
                                        //设定需要添加的自定义变量为Map<String, String>类型 并且放到UploadOptions第一个参数里面
                                        //上传策略中使用了自定义变量
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("Android", "报修");
                                        isCancelled = false;
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                                        String date = sdf.format(new Date());
                                        Log.d("...", "文件位置：" + selectMedia.get(i).getCompressPath());
                                        final String k = "upload/baoxiu/img/" + date + "/" + "android_" + System.currentTimeMillis() + "";
                                        //put第二个参数设置文件名
                                        uploadManager.put(selectMedia.get(i).getCompressPath(), k, s,
                                                new UpCompletionHandler() {
                                                    public void complete(String key,
                                                                         ResponseInfo info, JSONObject res) {
                                                        Logger.d(key + ",\r\n " + info
                                                                + ",\r\n " + res);
                                                        ImgIndex++;
                                                        if (info.isOK() == true) {
                                                            Logger.d(res.toString());
                                                            before_key += k + ";";
                                                            Logger.d(ImgIndex + "===============================");
                                                            if (ImgIndex == selectMedia.size()) {
                                                                ImgIndex = 0;
                                                                if (selectMedia1.size() == 0) {
                                                                    repair();
                                                                } else {
                                                                    for (int j = 0; j < selectMedia1.size(); j++) {
                                                                        //设定需要添加的自定义变量为Map<String, String>类型 并且放到UploadOptions第一个参数里面
                                                                        //上传策略中使用了自定义变量
                                                                        HashMap<String, String> map = new HashMap<String, String>();
                                                                        map.put("Android", "报修");
                                                                        isCancelled = false;
                                                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                                                                        String date = sdf.format(new Date());
                                                                        Log.d("...", "文件位置：" + selectMedia1.get(j).getCompressPath());
                                                                        final String k = "upload/baoxiu/img/" + date + "/" + "android_" + System.currentTimeMillis() + "";
                                                                        //put第二个参数设置文件名
                                                                        uploadManager.put(selectMedia1.get(j).getCompressPath(), k, s,
                                                                                new UpCompletionHandler() {
                                                                                    public void complete(String key,
                                                                                                         ResponseInfo info, JSONObject res) {
                                                                                        Logger.d(key + ",\r\n " + info
                                                                                                + ",\r\n " + res);
                                                                                        ImgIndex++;
                                                                                        if (info.isOK() == true) {
                                                                                            Logger.d(res.toString());
                                                                                            back_key += k + ";";
                                                                                            if (ImgIndex == selectMedia1.size()) {
                                                                                                repair();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }, new UploadOptions(map, null, false,
                                                                                        new UpProgressHandler() {
                                                                                            public void progress(String key, double percent) {
                                                                                                Log.i("qiniu", key + ": " + percent);
                                                                                            }

                                                                                        }, new UpCancellationSignal() {
                                                                                    @Override
                                                                                    public boolean isCancelled() {
                                                                                        return isCancelled;
                                                                                    }
                                                                                }));

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }, new UploadOptions(map, null, false,
                                                        new UpProgressHandler() {
                                                            public void progress(String key, double percent) {
                                                                Log.i("qiniu", key + ": " + percent);
                                                            }

                                                        }, new UpCancellationSignal() {
                                                    @Override
                                                    public boolean isCancelled() {
                                                        return isCancelled;
                                                    }
                                                }));

                                    }

                                }
                            });
                }
                break;
        }

    }

    //七牛上传配置
    public TreatFragment() {
        //断点上传
        String dirPath = "/storage/emulated/0/xzs";
        Recorder recorder = null;
        try {
            File f = File.createTempFile("qiniu_xxxx", ".tmp");
            Log.d("qiniu", f.getAbsolutePath().toString());
            dirPath = f.getParent();
            //设置记录断点的文件的路径
            recorder = new FileRecorder(dirPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String dirPath1 = dirPath;
        //默认使用 key 的url_safe_base64编码字符串作为断点记录文件的文件名。
        //避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
        KeyGenerator keyGen = new KeyGenerator() {
            public String gen(String key, File file) {
                // 不必使用url_safe_base64转换，uploadManager内部会处理
                // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
                String path = key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
                Log.d("qiniu", path);
                File f = new File(dirPath1, UrlSafeBase64.encodeToString(path));
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(f));
                    String tempString = null;
                    int line = 1;
                    try {
                        while ((tempString = reader.readLine()) != null) {
//                          System.out.println("line " + line + ": " + tempString);
                            Log.d("qiniu", "line " + line + ": " + tempString);
                            line++;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return path;
            }
        };

        Configuration config = new Configuration.Builder()
                // recorder 分片上传时，已上传片记录器
                // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .recorder(recorder, keyGen)
                .zone(Zone.zone2) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
        // 实例化一个上传的实例
        uploadManager = new UploadManager(config);
    }
}
