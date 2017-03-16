package com.yxld.xzs.adapter;
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

import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TakePhotoOptions;
import com.yxld.xzs.R;
import com.yxld.xzs.entity.AppWork;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yishangfei on 2017/3/14 0014.
 * 个人主页：http://yishangfei.me
 * Github:https://github.com/yishangfei
 */
public class RepairAdapter extends BaseQuickAdapter<AppWork,BaseViewHolder> {

    public RepairAdapter(List<AppWork> data) {
        super(R.layout.activity_repair_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AppWork item) {
        //报修单号
        String bx = "报修单号:　";
        String danhao =item.getBaoxiu_danhao();
        SpannableStringBuilder single = new SpannableStringBuilder(bx + danhao);
        single.setSpan(new ForegroundColorSpan(Color.parseColor("#909090")),
                0, bx.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //报修时间
        String sj = "报修时间:　";
        String lrtime =item.getBaoxiu_lrtime();
        SpannableStringBuilder time = new SpannableStringBuilder(sj + lrtime);
        time.setSpan(new ForegroundColorSpan(Color.parseColor("#909090")),
                0, sj.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //报修地点
        String address = item.getBaoxiu_loupan() + item.getBaoxiu_loudong() + "栋" + item.getBaoxiu_danyuan() + "单元" + item.getBaoxiu_fanghao() + "　" + item.getBaoxiu_didian();

        helper.setText(R.id.repair_time, time)
                .setText(R.id.repair_address,address)
        .setText(R.id.repair_single,single);

        if (item.getBaoxiu_status().equals("2") || item.getBaoxiu_status().equals("8")){
            helper.setText(R.id.repair_status,"待指派");
        }else if (item.getBaoxiu_status().equals("5")){
            helper.setText(R.id.repair_status,"待审核");
        }else {
            helper.setText(R.id.repair_status,"维修中");
        }
    }
}