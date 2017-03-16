package com.yxld.xzs.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import cn.hugeterry.updatefun.config.UpdateKey;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by yishangfei on 2016/11/3 0003.
 * 1、为了打开客户端的日志，便于在开发过程中调试，需要自定义一个 Application。
 * 并将自定义的 application 注册在 AndroidManifest.xml 文件中。<br/>
 * 2、为了提高 push 的注册率，您可以在 Application 的 onCreate 中初始化 push。你也可以根据需要，在其他地方初始化 push。
 *
 * @author wangkuiwei
 */

@DefaultLifeCycle(application = "com.yxld.xzs.base.DemoApplication",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)
public class DemoApplicationLike extends ApplicationLike {
    public DemoApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplication().getApplicationContext());
        //FIR 更新  Bug检测
//        FIR.init(getApplication());
        UpdateKey.API_TOKEN = "29e937a851bc867e8932c84ef85db3df";
        UpdateKey.APP_ID = "57fc8695959d6903920006ae";
        //下载方式:
        UpdateKey.DialogOrNotification = UpdateKey.WITH_DIALOG;//通过Dialog来进行下载
        //微信tinker
        TinkerInstaller.install(this);
    }
}