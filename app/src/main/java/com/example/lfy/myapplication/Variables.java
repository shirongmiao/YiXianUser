package com.example.lfy.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.HomePoint;
import com.example.lfy.myapplication.Bean.MineBean;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lfy on 2015/12/14.
 */
public class Variables {
    //屏幕高度
    public static int PhoneHeight;
    public static int PhoneWidth;


    //登陆之后获取的所有信息集合
    public static MineBean my;
    public static String DEVICE_ID;
    //    //登陆之后获取该自提点信息集合
    public static HomePoint point;
    //小红点的数量
    public static int count = 0;
    public static String www = "http://api.baifenxian.com/";
    public static String com = "token=9c565b08-7428-47f5-bd18-0cc2a80949db";
    //http://api.baifenxian.com/user/GetTopUpInfo?token=9c565b08-7428-47f5-bd18-0cc2a80949db

    //请求首页的轮播图point=idString
    public static String fragment_photo = www + "product/GetIndexImg?" + com;
    // //2.0首页分类商品显示
    public static String foot_photo = www + "product/GetHomeProduct?" + com;
    //yi ji fen lei
    public static String first_type = www + "product/GetType1?" + com;
    //er ji fen lei
    // http://api.baifenxian.com/product/SelectProductList?
    // token=9c565b08-7428-47f5-bd18-0cc2a80949db&
    // point=dbc1c95a-4f9e-4242-80ca-8f14f84606f1&
    // type=1&
    // orderby=1
    public static String second_product = www + "product/SelectProductList?" + com;
    //排序方式
    public static String second_orderby = www + "product/GetProductOrderBy?" + com;

    //获取二级分类
    public static String second_type = www + "product/GetProductType2_2?" + com;
    //获取版本信息
    public static String http_version = www + "home/UpdateVersion?" + com;
    //提货点的详细信息
    public static String http_getPoint = www + "point/GetPoint?" + com;
    //提货点的详细信息

    //查询自提点社区服务商信息
    public static String http_getPointPhoto = www + "point/GetPointPartner?" + com;


    //获取登录信息
    public static String http_login = www + "user/login?" + com;
    //注销登录信息
    public static String http_Logout = www + "user/Logout?" + com;

    //一鲜热卖  point=idString&count="10"
    public static String http_hot = www + "product/GetRankProduct?" + com;
    //注册短信http://api.baifenxian.com/umeng/sendSMS?token=9c565b08-7428-47f5-bd18-0cc2a80949db&phone=18268802188
//    public static String http_duanxin = "http://sms.chanzor.com:8001/sms.aspx";
    public static String http_duanxin = www + "umeng/sendSMS?" + com;
    //查询用户是否存在
    public static String http_phone = www + "user/RegisterCheck?" + com;
    //注册"http://testapi.baifenxian.com/user/register?token=9c565b08-7428-47f5-bd18-0cc2a80949db"
    public static String http_register = www + "user/register?" + com;

    public static String http_tuijian = www + "user/RegisterCheck?" + com;
    //某个提货点的所有商品信息
    public static String http_goods = www + "product/GetAllProduct?" + com;
    //更新个人信息
    public static String update_mine = www + "user/UpdateUserInfo?" + com;
    //根据用户id获取优惠券 &userid=
    public static String http_discount = www + "user/MyCoupon?" + com;
    //签到
    public static String http_sign = www + "user/SignIn?" + com;
    //查询收藏
    public static String http_select_collection = www + "product/GetCollectProduct?" + com;
    //查询单个商品的详情
    public static String goods_one = www + "product/GetProductInfo?" + com;
    //付款
    public static String hava_change = www + "pay/GetPayCharge?" + com;
    //提交订单
    public static String submit_order = www + "Order/InsertOrder?" + com;
    //查询订单
    public static String http_select_order = www + "order/GetOrder?" + com;
    //我的头像
    public static String Mine_Head = www + "user/UpdateUserImage?" + com;
    //收藏
    public static String http_collection = www + "user/AddCollect?" + com;
    //取消收藏
    public static String http_uncollection = www + "user/DeleteCollect?" + com;
    //热门搜索
    public static String http_hot_search = www + "product/GetHotSearch?" + com;
    //搜索
    public static String http_search = www + "product/SearchResult?" + com;
    //检查该用户是否已经购买过新用户专享商品：
    // userid=0bd06066-0820-4f53-ba09-c9c54862172a&search=新用户
    public static String http_new_search = www + "order/GetNewOrder?" + com;

    //地址,提货点——所有的
    public static String http_all_pointid = www + "point/GetAllPoint?" + com;


    public static String http_applay = www + "user/SetUpShop?" + com;
    public static String http_feedback = www + "user/Feedback?" + com;

    //取消订单
    public static String http_delete_order = www + "order/DeleteOrder?" + com;

    //&userid=0bd06066-0820-4f53-ba09-c9c54862172a&search=新用户
    //查询是否为新用户
    public static String http_new_user = www + "order/GetNewOrder?" + com;

    //查询一周内领取过优惠券
    public static String http_select_huoqu = www + "home/CheckCoupon?" + com;
    //获取优惠券
    public static String http_huoqu = www + "home/GetCoupon?" + com;
    //获取服务器时间戳 http://api.baifenxian.com/home/GetTime?token=9c565b08-7428-47f5-bd18-0cc2a80949db
    public static String http_time = www + "home/GetTime?" + com;
    //地图定位http请求方式: longitude=120.33387&latitude=30.314652
    public static String http_baidu = www + "point/SelectLocation?" + com;
    public static String http_baidu_one = www + "point/SelectLocationOneKm?" + com;
    //地址请求方式 user/SelectAddress?token=&customerID=0bd06066-0820-4f53-ba09-c9c54862172a
    public static String http_all_address = www + "user/SelectAddress?" + com;
    //地址删除 /user/DeleteAddress?token=&addressID=
    public static String http_delete_address = www + "user/DeleteAddress?" + com;
    //地址更新
    public static String http_update_address = www + "user/UpdateAddress?" + com;
    //插入地址
    // customerID=0bd06066-0820-4f53-ba09-c9c54862172a&name=&pointid=&city=&phone=&district=&address=&sex=
    public static String http_insert_address = www + "user/InsertAddress?" + com;
    //设为默认地址
    public static String http_Default_address = www + "user/UpdateDefaultAddress?" + com;
    //通过adressID查询地址详情/user/SelectAddressInfo
    public static String http_address_particular = www + "user/SelectAddressInfo?" + com;
    //更改密码
    //user/ForgetPwd? &phone=18136775940 &password=654321
    public static String http_update_password = www + "user/ForgetPwd?" + com;
    //充值面额   point=dbc1c95a-4f9e-4242-80ca-8f14f84606f1
    public static String http_money = www + "user/GetTopUpInfo?" + com;
    //插入充值记录InsertTopUpRecord(string token,string customerId, string topUpInfoId,string topUpOrderNo)
    public static String http_InsertTopUpRecord = www + "user/InsertTopUpRecord?" + com;
    //查看充值记录SelectTopUpRecord(string token,string customerId,int timestamp)
    public static String http_SelectTopUpRecord = www + "user/SelectTopUpRecord?" + com;
    //查询余额SelectTopUpPrice(string token,string customerId)
    public static String http_Select_yue = www + "user/SelectTopUpPrice?" + com;

    //余额支付，修改余额与订单状态TopUpPay(string token, string orderNo,string customerId)
    public static String http_TopUpPay = www + "order/TopUpPay?" + com;

    //检查支付密码CheckTopUpPwd(string token, string customerId, string TopUpPwd);
    public static String http_select_password = www + "user/CheckTopUpPwd?" + com;

    //设置支付密码，存在则更新支付密码InsertTopUpPwd(string token, string customerId, string TopUpPwd);
    public static String http_setting_password = www + "user/InsertTopUpPwd?" + com;

    //查询购物车的信息
    //public ActionResult GetCart(string token, string CustomerID, string point)
    public static String http_getCar = www + "product/GetCart?" + com;
    //  //清空购物车
    //public ActionResult DeleteCart(string token, string CustomerID, string point)
    public static String http_DeleteCar = www + "product/DeleteCart?" + com;

    //http://api.baifenxian.com/product/InsertCart?
    // token=9c565b08-7428-47f5-bd18-0cc2a80949db&
    // CustomerID=13432607-02f6-46fe-a3c1-0bc62d35cea2&
    // ProductID=8e08cce9-3c6f-475d-b276-005605546c5c&
    // point=e1a1c063-4352-4f51-a7cd-8c690ba11523&
    // type=1
    public static String http_InsertCar = www + "product/InsertCart?" + com;
    //获取团购商品信息
    public static String http_GetTuanProduct = www + "product/GetTuanProduct?" + com;
    //开团插入团购订单
    public static String http_InsertTuanOrder = www + "order/InsertTuanOrder?" + com;
    //通过服务站查询团购,1为按时间，2为按进度
    public static String GetTuanOrderByPoint = www + "order/GetTuanOrderByPoint?" + com;
    //用户参团
    public static String JoinTuan = www + "order/JoinTuan?" + com;
    //删除团购订单
    public static String DeleteTuanOrder = www + "order/DeleteTuanOrder?" + com;
    //获取用户团购订单
    public static String GetTuanOrder = www + "order/GetTuanOrder?" + com;
    //通过团ID查团购详情
    public static String GetTuanOrderInfo = www + "order/GetTuanOrderInfo?" + com;
    //请求配送状态  orderId=
    public static String UpdateOrderType2 = www + "order/UpdateOrderType2?" + com;
    //商家版更新订单状态
    public static String SelectProcess = www + "order/SelectProcess?" + com;
    //查看订单评论
    public static String GetEvaluate = www + "order/GetEvaluate?" + com;
    //上传评价
    public static String SendEvaluate = www + "order/SendEvaluate?" + com;
    //获取商品评价列表
    public static String ProductEvaluate = www + "product/ProductEvaluate?" + com;
    //显示评价数量和平均值
    public static String EvaluateCount = www + "product/EvaluateCount?" + com;
    //获取推荐商品
    public static String RecommendProduct = www + "product/RecommendProduct?" + com;

    // 需要setContentView之前调用
    public static void setTranslucentStatus(Activity con) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            con.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            SystemStatusManager tintManager = new SystemStatusManager(con);
//            tintManager.setStatusBarTintEnabled(true);
//            // 设置状态栏的颜色
//            tintManager.setStatusBarTintResource(R.color.kong);
//            con.getWindow().getDecorView().setFitsSystemWindows(true);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            con.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                int options = 100;
                while (baos.toByteArray().length / 1024 > 50) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                    baos.reset();//重置baos即清空baos
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                    options -= 10;//每次都减少10
                }

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    //view转化Bitmap
    public static Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
    }

    //时间比较
    public static boolean compare_date(String DATE1, String DATE2) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");
        boolean flag = false;
        try {
            Date dt1 = sDateFormat.parse(DATE1);
            Date dt2 = sDateFormat.parse(DATE2);
            if (dt1.getTime() < dt2.getTime()) {
                System.out.println(dt1.getTime() + "dt1 在dt2前,还差xxxx天" + dt2.getTime());
                flag = true;
            } else if (dt1.getTime() <= dt2.getTime()) {
                System.out.println("dt1在dt2后");
                flag = false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return flag;
    }

    //微信分享
    public static void WXshare(Context context, int type, String title, String description, String url, int imgId) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, "wxf4d91338ae39b676", false);
        if (!api.isWXAppInstalled()) {
            Toast.makeText(context, "您还未安装微信客户端",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        //"http://www.baifenxian.com/user/partner.html";
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        //R.mipmap.all_logo
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), imgId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumb.compress(Bitmap.CompressFormat.PNG, 100, baos);
        msg.thumbData = baos.toByteArray();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = type;
        api.sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

}
