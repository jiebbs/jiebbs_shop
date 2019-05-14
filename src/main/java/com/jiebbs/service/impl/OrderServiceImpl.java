package com.jiebbs.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.Car;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.dao.*;
import com.jiebbs.pojo.*;
import com.jiebbs.service.IOrderService;
import com.jiebbs.util.BigDecimalUtil;
import com.jiebbs.util.DateUtil;
import com.jiebbs.util.PropertiesUtil;
import com.jiebbs.vo.OrderItemVO;
import com.jiebbs.vo.OrderProductVO;
import com.jiebbs.vo.OrderShippingVO;
import com.jiebbs.vo.OrderVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 订单接口实现类
 * @author weijie
 * @version v1.0 2019-05-08
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse createOrder(Integer userId,Integer shippingId){
        if(null==shippingId){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //从购物车中获取产品数据
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        //遍历已经被勾选的产品，并且进行组装
        ServerResponse<List<OrderItem>> resp = getCartOrderItem(userId,cartList);
        //判断响应是否成功
        if(!resp.isSuccess()){
            return resp;
        }
        List<OrderItem> orderItemList = resp.getData();
        //组装生成订单
        Order order = assembleOrder(userId,shippingId,orderItemList);
        int orderResult = orderMapper.insert(order);
        if(orderResult>0){
            //对orderItem插入订单号
            for(OrderItem orderItem:orderItemList){
                orderItem.setOrderNo(order.getOrderNo());
                //更新orderItem到数据表
            }
            //mybatis批量插入数据库
            int brenchInsertResult = orderItemMapper.brenchInsertOrderItem(orderItemList);
            if(brenchInsertResult>0){
                //更新成功减少商品库存
                this.reduceProductStock(orderItemList);
                //清空购物车
                cartMapper.brenchDeleteCartByCartId(cartList);
                //组装返回订单明细
                OrderVO orderVO = this.assembleOrderVO(order,orderItemList);

                return ServerResponse.createBySuccessMessageAndData("商品数据插入成功",orderVO);
            }else{
                return ServerResponse.createByErrorMessage("商品数据插入失败");
            }
        }else {
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
    }

    public ServerResponse cancelOrder(Integer userId,Long orderNo){
        if(null == orderNo){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //校验订单是否是已支付状态
        Order order = orderMapper.selectByUserIdOrderNo(userId,orderNo);
        if(null!=order){
            if(order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()){
                return ServerResponse.createByErrorMessage("订单已支付，无法取消");
            }
            Order cancelOrder = new Order();
            cancelOrder.setId(order.getId());
            cancelOrder.setStatus(Const.OrderStatusEnum.CANCALED.getCode());
            int updateStatusResult = orderMapper.updateByPrimaryKey(cancelOrder);
            return updateStatusResult>0?ServerResponse.createBySuccessMessage("订单取消成功"):
                    ServerResponse.createByErrorMessage("订单取消失败");
        }
        return ServerResponse.createByErrorMessage("订单不存在或已删除");
    }


    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVO orderProductVO = new OrderProductVO();
        //获取购物车中勾选的产品
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        if(CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车中没有勾选的产品");
        }
        //将勾选的产品转回为订单物品
        ServerResponse resp = this.getCartOrderItem(userId,cartList);
        if(!resp.isSuccess()){
            return resp;
        }
        List<OrderItem> orderItemList = (List<OrderItem>)resp.getData();
        //组装orderItemVO
        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        BigDecimal totalPrice = new BigDecimal("0");
        for(OrderItem orderItem:orderItemList){
            OrderItemVO orderItemVO = this.assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
            //计算勾选产品的购物车总价
            totalPrice = BigDecimalUtil.add(totalPrice,orderItemVO.getTotalPrice());
        }
        orderProductVO.setOrderItemList(orderItemList);
        orderProductVO.setTotalPrice(totalPrice.toString());

        return ServerResponse.createBySuccessData(orderProductVO);
    }


    public ServerResponse getOrderDetail(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdOrderNo(userId,orderNo);
        if(null==order){
            return ServerResponse.createByErrorMessage("查询用户无此订单");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdOrderNo(userId,orderNo);
        //组装orderVO
        OrderVO orderVO = this.assembleOrderVO(order,orderItemList);
        return ServerResponse.createBySuccessData(orderVO);
    }




    public ServerResponse pay(Integer userId,Long orderNo,String path){
        //创建数据承载的map
        Map<String,String> resultMap = Maps.newHashMap();
        //校验订单是否存在
        Order order = orderMapper.selectByUserIdOrderNo(userId,orderNo);
        if(null == order){
            return ServerResponse.createByErrorMessage("该订单不存在或已删除");
        }
        //查询到订单将订单号存入map
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));

        // 支付宝当面付集成
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("jiebbs_shop扫码支付：订单号：").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单：").append(outTradeNo).append(",购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdOrderNo(userId,orderNo);
        if(CollectionUtils.isNotEmpty(orderItemList)){
            for(OrderItem orderItem:orderItemList){
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            //        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
            // 创建好一个商品后添加至商品明细列表
            //        goodsDetailList.add(goods1);
                GoodsDetail goods = GoodsDetail.newInstance(orderItem.getId().toString(),orderItem.getProductName(),orderItem.getCurrentUnitPrice().longValue() ,orderItem.getQuantity());
                goodsDetailList.add(goods);
            }
        }
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        Configs.init("zfbinfo.properties");

        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                //校验传入的二维码上传路径path
                File qrCodeFolder = new File(path);
                //如果此路径不存在则创建此路径
                if(!qrCodeFolder.exists()){
                    //赋予可写权限并且创建文件夹
                    qrCodeFolder.setWritable(true);
                    qrCodeFolder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String qrFilePath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                //支付宝封装好的qrCode生成工具类
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrFilePath);
                log.info("qrPath:" + qrFilePath);
                //上传生成的qrCode到文件服务器
                File targetFile = new File(path,qrFileName);


                //FTPUtil.fileUploads(Lists.newArrayList(targetFile));

                //拼装返回的qrCode的Url
                String qrCodeUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
                resultMap.put("qrCodeUrl",qrCodeUrl);
                return ServerResponse.createBySuccessMessageAndData("支付宝预下单成功",resultMap);
            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return null;
    }

    public ServerResponse alipayCallback(Map<String,String> covertDateMap){
        //获取回调信息
        Long orderNo = Long.valueOf(covertDateMap.get("out_trade_no")); //支付宝外部订单号 | 内部订单号
        String tradeNo = covertDateMap.get("trade_no"); //支付宝交易号
        String tradeStatus = covertDateMap.get("trade_status"); //交易状态

        //校验该订单是否存在
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(null == order){
            return ServerResponse.createByErrorMessage("该交易订单不存在,回调忽略");
        }
        //判断这个订单是否已经是已支付状态
        if(order.getStatus()>= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        }
        //判断返回的订单状态
        if(StringUtils.equals(tradeStatus,Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS)){
            //交易成功，更改订单交易状态已付款
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            //记录交易时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                order.setPaymentTime(simpleDateFormat.parse(covertDateMap.get("gmt_payment")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //将状态到数据库
            int orderUpdateStatus = orderMapper.updateByPrimaryKeySelective(order);
        }

        log.info("订单状态更新成功，状态变更为：{}",order.getStatus());
        //记录订单信息
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setUserId(order.getUserId());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        //更新数据到数据库
        int payInfoResult = payInfoMapper.insert(payInfo);
        return  ServerResponse.createBySuccess();
    }


    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderNo){
        if(null==orderNo){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Order order = orderMapper.selectByUserIdOrderNo(userId,orderNo);
        if(null==order){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccessData(true);
        }
        log.info("订单状态为：{}",order.getStatus());
        return ServerResponse.createByError();
    }

    //购物车产品对象转回为订单列表商品对象
    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList = Lists.newArrayList();
        //判断购物车中是否有商品
        if (CollectionUtils.isNotEmpty(cartList)){
            for(Cart cart:cartList){
                OrderItem orderItem = new OrderItem();
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                //判断产品是否在售
                if(Const.ProductStatusEnum.ON_SALE.getStatus()!= product.getStatus()){
                    return ServerResponse.createByErrorMessage("产品"+product.getName()+"不是在售状态");
                }
                //校验库存
                //若大于库存则修改为库存数量
                if(cart.getQuantity()>=product.getStock()){
                   return ServerResponse.createByErrorMessage("产品"+product.getName()+"库存不足");
                }
                orderItem.setUserId(userId);
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setProductImage(product.getMainImage());
                orderItem.setCurrentUnitPrice(product.getPrice());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setTotalPrice(BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice(),orderItem.getQuantity()));
                orderItemList.add(orderItem);
            }
            return ServerResponse.createBySuccessData(orderItemList);
        }else {
            return ServerResponse.createByErrorMessage("未勾选产品，请勾选产品后再次提交订单");
        }
    }

    /**
     * 组装订单详情的值对象
     * @param order
     * @param orderItemList
     * @return
     */
    public OrderVO assembleOrderVO(Order order,List<OrderItem> orderItemList){
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        orderVO.setPaymentTypeDesc(Const.PaymentTypeEnum.getPaymentTypeEnumDesc(order.getPaymentType()));
        orderVO.setPostage(order.getPostage());
        orderVO.setStatus(order.getStatus());
        orderVO.setStatusDesc(Const.OrderStatusEnum.getOrderStatusEnumDesc(order.getStatus()));
        orderVO.setShippingId(order.getShippingId());
        //设置详细地址
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping!=null){
            orderVO.setReceiverName(shipping.getReceiverName());
            //添加收货地址详情
            orderVO.setShippingVO(assembleShippingVO(shipping));
        }
        orderVO.setPaymentTime(DateUtil.date2str(order.getPaymentTime()));
        orderVO.setSendTime(DateUtil.date2str(order.getSendTime()));
        orderVO.setEndTime(DateUtil.date2str(order.getEndTime()));
        orderVO.setCreateTime(DateUtil.date2str(order.getCreateTime()));
        orderVO.setCloseTime(DateUtil.date2str(order.getCloseTime()));

        orderVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        //组装OrderItem
        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        for (OrderItem orderItem:orderItemList){
            orderItemVOList.add(this.assembleOrderItemVO(orderItem));
        }
        orderVO.setOrderItemVOList(orderItemVOList);

        return orderVO;
    }

    /**
     * 组装orderItem值对象
     * @param orderItem
     * @return
     */
    public OrderItemVO assembleOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setOrderNo(orderItem.getOrderNo());
        orderItemVO.setCreateTime(DateUtil.date2str(orderItem.getCreateTime()));
        orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVO.setProductId(orderItem.getProductId());
        orderItemVO.setProductImage(orderItem.getProductImage());
        orderItemVO.setProductName(orderItem.getProductName());
        orderItemVO.setQuantity(orderItem.getQuantity());
        orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        return orderItemVO;
    }



    /**
     * 组装订单详情中订单收货地址值对象
     * @param shipping
     * @return
     */
    private OrderShippingVO assembleShippingVO(Shipping shipping){
        OrderShippingVO orderShippingVO = new OrderShippingVO();
        orderShippingVO.setReceiverName(shipping.getReceiverName());
        orderShippingVO.setReceiverAddress(shipping.getReceiverAddress());
        orderShippingVO.setReceiverCity(shipping.getReceiverCity());
        orderShippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
        orderShippingVO.setReceiverMobile(shipping.getReceiverMobile());
        orderShippingVO.setReceiverProvince(shipping.getReceiverProvince());
        orderShippingVO.setReceiverZip(shipping.getReceiverZip());
        orderShippingVO.setReceiverPhone(shipping.getReceiverPhone());
        return orderShippingVO;
    }


    /**
     * 组装订单方法
     * @param userId
     * @param shippingId
     * @param orderItemList
     * @return
     */
    private Order assembleOrder(Integer userId,Integer shippingId,List<OrderItem> orderItemList){
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(getOrderTotalPrice(orderItemList));
        order.setPostage(0); //默认为0
        order.setPaymentType(Const.PaymentTypeEnum.PAY_ONLINE.getCode()); //默认为在线支付
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode()); //生成订单默认未支付
        return order;
    }

    /**
     * 计算订单总价
     * @param orderItemList
     * @return
     */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal totalPrice = new BigDecimal("0");
        //计算订单总价
        for(OrderItem orderItem:orderItemList){
            totalPrice = BigDecimalUtil.add(totalPrice,orderItem.getTotalPrice());
        }
        return totalPrice;
    }

    /**
     * 减少产品库存
     * @param orderItemList
     */
    private void reduceProductStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem:orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    /**
     * 生成订单方法
     * @return
     */
    private Long generateOrderNo(){
        Long currentTime = System.currentTimeMillis();
        //使用当前时间加上当前时间取余来创建订单号
        return currentTime+new Random().nextInt(100);
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
}
