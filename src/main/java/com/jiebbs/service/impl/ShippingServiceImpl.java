package com.jiebbs.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.dao.ShippingMapper;
import com.jiebbs.pojo.Shipping;
import com.jiebbs.service.IShippingService;
import com.jiebbs.vo.ShippingVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public ServerResponse updateShippingAddress(Integer userId, Shipping shipping){
        if(null==shipping){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping newShipping = new Shipping();
        newShipping.setId(shipping.getId());
        newShipping.setUserId(userId); //防止横向越权重新赋值user_id
        newShipping.setReceiverName(shipping.getReceiverName());
        newShipping.setReceiverPhone(shipping.getReceiverPhone());
        newShipping.setReceiverMobile(shipping.getReceiverMobile());
        newShipping.setReceiverProvince(shipping.getReceiverProvince());
        newShipping.setReceiverCity(shipping.getReceiverCity());
        newShipping.setReceiverDistrict(shipping.getReceiverDistrict());
        newShipping.setReceiverAddress(shipping.getReceiverAddress());
        newShipping.setReceiverZip(shipping.getReceiverZip());
        int shippingResult = shippingMapper.updateByPrimaryKeyUserIdSelective(newShipping);
        return shippingResult>0?ServerResponse.createBySuccessMessage("更新收货地址成功"):
                ServerResponse.createByErrorMessage("更新收货地址失败");
    }


    public ServerResponse getShippingAddressDetail(Integer userId,Integer shippingId){
        if(null==shippingId){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shippingResult = shippingMapper.selectShippingAddressDetail(userId,shippingId);
        if(null==shippingResult){
            return ServerResponse.createByErrorMessage("收货地址已删除");
        }
        return ServerResponse.createBySuccessMessageAndData("查询收货地址成功",shippingResult);
    }

    public ServerResponse<PageInfo<ShippingVO>> listShippingAddress(Integer userId, Integer pageNum, Integer pageSize){
        List<ShippingVO> shippingVOList = Lists.newArrayList();
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        //查询借助AOP插入sql分页语句
        List<Shipping> shippingList= shippingMapper.selectShippingAddressByUserId(userId);
        //将返回数据进行封装
        if(CollectionUtils.isNotEmpty(shippingList)){
            for(Shipping shipping:shippingList){
                ShippingVO shippingVo = new ShippingVO();
                shippingVo.setId(shipping.getId());
                shippingVo.setReceiverName(shipping.getReceiverName());
                shippingVo.setReceiverAddress(shipping.getReceiverAddress());
                shippingVo.setReceiverMobile(shipping.getReceiverMobile());
                shippingVo.setReceiverProvince(shipping.getReceiverProvince());
                shippingVo.setReceiverCity(shipping.getReceiverCity());
                shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
                shippingVOList.add(shippingVo);
            }
        }
        //建立分页信息
        PageInfo<ShippingVO> pageInfo = new PageInfo<>(shippingVOList);
        return ServerResponse.createBySuccessData(pageInfo);
    }
}
