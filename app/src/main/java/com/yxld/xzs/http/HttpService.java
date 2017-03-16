package com.yxld.xzs.http;


import com.yxld.xzs.base.BaseResultEntity;
import com.yxld.xzs.base.BaseResultEntity1;
import com.yxld.xzs.base.BaseResultEntity2;
import com.yxld.xzs.entity.AppAssign;
import com.yxld.xzs.entity.AppCode;
import com.yxld.xzs.entity.AppHome;
import com.yxld.xzs.entity.AppWork;
import com.yxld.xzs.entity.AppLogin;
import com.yxld.xzs.entity.AppOrder;
import com.yxld.xzs.entity.AppPeisongorder;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 作者：yishangfei on 2016/12/31 0031 10:16
 * 邮箱：yishangfei@foxmail.com
 */
public interface HttpService {
    //登陆接口
    @GET("wygl/admin/androidAdmin_adminLogin?")
    Observable<BaseResultEntity<AppLogin>> login(@Query("userName") String phone, @Query("passWord") String pwd);

    //修改密码接口
    @GET("wygl/admin/androidAdmin_adminUpdatePwd?")
    Observable<BaseResultEntity<AppLogin>> change(@Query("userName") String phone, @Query("passWord") String pwd,@Query("newPassWord") String npwd);

    //修改配送人接单状态
    @GET("wygl/mall/androidPeisong_mergePeisongState")
    Observable<BaseResultEntity<String>> state(@Query("peisong.cxwyPeisongId") int peisongid, @Query("peisong.cxwyPeisongState") String state);

    //待抢单接口
    @GET("wygl/mall/androidPeisongOrder_findDaiQiangDanOrder")
    Observable<BaseResultEntity1<AppHome>> grab(@Query("peisong.cxwyPeisongId") int peisongid,@Query("xiaoquId") int xiaoquId);

    //抢单按钮接口
    @GET("wygl/mall/androidPeisongOrder_peiSongQiangDan")
    Observable<BaseResultEntity<AppOrder>> single(@Query("peisongorder.peisongOrderPeisongId") int peisongid, @Query("peisongorder.peisongOrderOrderBianhao") int bianhao,@Query("xiaoquId") int xiaoquId);

    //待取货接口
    @GET("wygl/mall/androidPeisongOrder_findDaiQuHuoOrder")
    Observable<BaseResultEntity1<AppHome>> pick(@Query("peisong.cxwyPeisongId") int peisongid, @Query("xiaoquId") int xiaoquId);

    //取货按钮接口
    @GET("wygl/mall/androidPeisongOrder_peiSongQuHuo")
    Observable<BaseResultEntity<AppOrder>> take(@Query("peisongorder.peisongOrderPeisongId") int peisongid, @Query("peisongorder.peisongOrderOrderBianhao") int bianhao);

    //待送达接口
    @GET("wygl/mall/androidPeisongOrder_findDaiSongdaOrder")
    Observable<BaseResultEntity1<AppHome>> send(@Query("peisong.cxwyPeisongId") int peisongid,@Query("xiaoquId") int xiaoquId);

    //送达按钮接口
    @GET("wygl/mall/androidPeisongOrder_peiSongSongDa")
    Observable<BaseResultEntity<AppOrder>> reach(@Query("peisongorder.peisongOrderPeisongId") int peisongid, @Query("peisongorder.peisongOrderOrderBianhao") int bianhao);

    //获取配送人员总收入接口
    @GET("wygl/mall/androidPeisong_PeisongrenTotalShouruById")
    Observable<BaseResultEntity<String>> total(@Query("id") int peisongid);

    //获取工作汇总已完成接口和取配送明细接口
    @GET("wygl/mall/androidPeisongOrder_PeisongOrderDestail")
    Observable<BaseResultEntity<List<AppPeisongorder>>> complete(@Query("id") int peisongid);

    //获取工作汇总取消单接口
    @GET("wygl/mall/androidPeisongOrder_PeisongOrderCancelOrder")
    Observable<BaseResultEntity<List<AppPeisongorder>>> cancel(@Query("id") int peisongid);

    // 获取工作汇总异常单接口
    @GET("wygl/mall/androidPeisongOrder_PeisongOrderYiChangOrder")
    Observable<BaseResultEntity<List<AppPeisongorder>>> abnormal(@Query("id") int peisongid);

    // 获取客服电话
    @GET("wygl/mall/androidPeisongOrder_getKefuPhoneByXiaoqu")
    Observable<BaseResultEntity<String>> hotline(@Query("xiaoqu") String xiaoqu);

    //夜间管理
    @GET("wygl/mall/androidPeisongOrder_findTodayQuhuoByFuzeren")
    Observable<BaseResultEntity<List<AppPeisongorder>>> night(@Query("id") int id);

    //特殊门禁
    @GET("door/coeds/tsqrcode")
    Observable<BaseResultEntity<AppCode>> quard(@Query("bName") String name, @Query("bPhone") String tel);

    //报修  工单验收
    @GET("wygl/daily/androidBaoxiu_andriodBaoxiulist.action")
    Observable<BaseResultEntity<List<AppWork>>> work(@Query("adminId") int adminId);

    //报修  获取指派维修人
    @GET("wygl/daily/androidBaoxiu_androidRepair.action")
    Observable<BaseResultEntity<List<AppAssign>>> assign(@Query("answer") int answer);

    //报修  项目主管指派维修人
    @GET("wygl/daily/androidBaoxiu_andriodAssign.action")
    Observable<BaseResultEntity<String>> point(@Query("answer") int answer,@Query("repair") int repair,@Query("baoxiuId")int id);

    //报修 项目主管验收
    @GET("wygl/daily/androidBaoxiu_androidCheck.action")
    Observable<BaseResultEntity<String>> check(@Query("answer") int answer,@Query("opinion") String opinion,@Query("order") String order,@Query("baoxiuId") int baoxiuId);

    //报修七牛token
    @GET("wygl/qiniu/qiniu_getQiniuToken.action?")
    Observable<BaseResultEntity2<String>> token();

    //报修  维修人维修
    @GET("wygl/daily/androidBaoxiu_androidFinish.action")
    Observable<BaseResultEntity<String>> maintain(@Query("baoxiuId") int baoxiuId, @Query("opinion") String opinion,@Query("order") String order);
}
