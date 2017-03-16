package com.yxld.xzs.base;

import android.support.v4.app.Fragment;

/**
 * 作者：yishangfei on 2017/1/6 0006 10:37
 * 邮箱：yishangfei@foxmail.com
 */
public abstract class BaseFragment extends Fragment{
    protected boolean isVisible;
    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }
    protected void onVisible(){
        lazyLoad();
    }
    protected abstract void lazyLoad();
    protected void onInvisible(){}
}