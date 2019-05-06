package com.jiebbs.service.impl;

import com.google.common.collect.Maps;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.dao.ShippingMapper;
import com.jiebbs.pojo.Shipping;
import com.jiebbs.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 收货地址服务接口实现类
 * @author weijie
 * @version v1.0 2019-05-06
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse addShippingAddress(Integer userId, Shipping shipping){
        if(null==shipping){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        shipping.setUserId(userId);
        int shippingResult = shippingMapper.insert(shipping);
        if(shippingResult>0){
            Map resultMap = Maps.newHashMap();
            resultMap.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccessMessageAndData("新增收货地址成功",resultMap);
        }
        return  ServerResponse.createByErrorMessage("新增收货地址失败");
    }


    public ServerResponse deleteShippingAddress(Integer userId, Integer shippingId){
        if(null==shippingId){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int shippingResult = shippingMapper.deleteByPrimaryKey(shippingId);
        return shippingResult>0?ServerResponse.createBySuccessMessage("删除收货地址成功"):
                ServerResponse.createByErrorMessage("删除收货地址失败");
    }

    public ServerResponse updateShippingAddress(Integer userId, Integer shippingId,Shipping shipping){
        if(null==shipping || null==shippingId){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping newShipping = new Shipping();
        newShipping.setId(shippingId);
        newShipping.setReceiverName(shipping.getReceiverName());
        newShipping.setReceiverPhone(shipping.getReceiverPhone());
        newShipping.setReceiverMobile(shipping.getReceiverMobile());
        newShipping.setReceiverProvince(shipping.getReceiverProvince());
        newShipping.setReceiverCity(shipping.getReceiverCity());
        newShipping.setReceiverDistrict(shipping.getReceiverDistrict());
        newShipping.setReceiverAddress(shipping.getReceiverAddress());
        newShipping.setReceiverZip(shipping.getReceiverZip());
        int shippingResult = shippingMapper.updateByPrimaryKeySelective(newShipping);
        return shippingResult>0?ServerResponse.createBySuccessMessage("更新收货地址成功"):
                ServerResponse.createByErrorMessage("更新收货地址失败");
    }
}
