package com.jiebbs.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.User;
import com.jiebbs.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;


/**
 * 前台订单模块接口
 * @author weijie
 * @version v1.0 2019-05-08
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static Logger log = LoggerFactory.getLogger(OrderController.class);

    @Resource(name="iOrderService")
    private IOrderService iOrderService;

    /**
     * 创建订单接口
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping(value = "create_order.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse createOrder(HttpSession session,Integer shippingId){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,需要进行登录");
        }

        return iOrderService.createOrder(user.getId(),shippingId);
    }

    /**
     * 取消订单接口
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "cancel_order.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse cancelOrder(HttpSession session,Long orderNo){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,需要进行登录");
        }
        return iOrderService.cancelOrder(user.getId(),orderNo);
    }

    /**
     * 查看产生的订单的产品预览接口
     * @param session
     * @return
     */
    @RequestMapping(value = "get_order_cart_product.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,需要进行登录");
        }

        return iOrderService.getOrderCartProduct(user.getId());
    }

    /**
     * 订单物品详情接口
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "get_order_detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getOrderDetail(HttpSession session,Long orderNo){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,需要进行登录");
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    /**
     * 订单支付接口
     * @param session
     * @param request
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "pay.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse pay(HttpSession session, HttpServletRequest request,Long orderNo){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,需要进行登录");
        }
        //获取文件上传路径
        String path = request.getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(),orderNo,path);
    }

    /**
     * 支付宝回调接口
     * @param request
     * @return
     */
    @RequestMapping(value = "alipay_callback.do",method = RequestMethod.POST)
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        //转换后的数据承载map
        Map<String,String> covertDateMap = Maps.newHashMap();

        //获取支付宝回调数据
        Map<String,String[]> alipayCallbackData = request.getParameterMap();
        //获取支付宝返回参数map中的key
        Iterator iterator = alipayCallbackData.keySet().iterator();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            String[] values = alipayCallbackData.get(key);
            //遍历转换键数组为一个String值
            String covertValue = "";
            for(int i=0;i<values.length;i++){
                //非最后一个元素,拼接后加个",",否则不加
                covertValue = i == (values.length-1)?covertValue+values[i]:covertValue+values[i]+",";
            }
            covertDateMap.put(key,covertValue);
        }
        //打印日志
        log.info("支付宝回调,sign:{},trade_status:{},参数：{}",covertDateMap.get("sign"),covertDateMap.get("trade_status"),covertDateMap.toString());

        //验证回调的正确性,是不是支付宝发的，并且还要避免重复通知
        //同时支付宝接入文档验签要求去除sign和sign_type这2个字段，并且alipayTradeServiceImpl中已经帮我们去除了sign字段，所以我们要自行去除sign_type
        covertDateMap.remove("sign_type");

        //进行验签
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(covertDateMap, Configs.getAlipayPublicKey(),"utf-8", Configs.getSignType());
            //判断验签是否通过
            if(!alipayRSACheckedV2){
                //验签不通过
                return ServerResponse.createByErrorMessage("非法请求");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调验签错误,验签参数：{},支付宝公钥：{},设置字符集：{},验签类型：{}",covertDateMap.toString(),Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            log.error("错误信息:{}",e);
        }
        //TODO 按照支付宝验签要求验证订单号等一系列数据是否正确

        ServerResponse resp = iOrderService.alipayCallback(covertDateMap);
        //回调成功返回success，错误返回failed
        return resp.isSuccess()?Const.AlipayCallback.RESPONSE_SUCCESS:Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 轮询订单状态接口
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "query_order_pay_status.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session,Long orderNo){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,需要进行登录");
        }
        ServerResponse resp = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(resp.isSuccess()){
            return resp;
        }
        return ServerResponse.createBySuccessData(false);
    }
}
