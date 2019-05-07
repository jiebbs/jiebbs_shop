package com.jiebbs.controller.portal;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Shipping;
import com.jiebbs.pojo.User;
import com.jiebbs.service.IShippingService;
import com.jiebbs.vo.ShippingVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 前端收货地址模块
 * @author weijie
 * @version v1.0 2019-05-06
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Resource(name = "iShippingService")
    private IShippingService iShippingService;

    /**
     * 新增收货地址接口
     * @param session
     * @param shipping
     * @return
     */
    @RequestMapping(value = "add_shipping_address.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse addShippingAdress(HttpSession session, Shipping shipping){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }

        return iShippingService.addShippingAddress(user.getId(),shipping);
    }

    /**
     * 删除收货地址接口
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping(value = "delete_shipping_address.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse deleteShippingAddress(HttpSession session,Integer shippingId){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }
        return iShippingService.deleteShippingAddress(user.getId(),shippingId);
    }

    /**
     * 更新收货地址接口
     * @param session
     * @param shipping
     * @return
     */
    @RequestMapping(value = "update_shipping_address.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse updateShippingAddress(HttpSession session,Shipping shipping){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }
        return iShippingService.updateShippingAddress(user.getId(),shipping);
    }

    /**
     * 查询收货地址接口
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping(value = "get_shipping_address_detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse selectShippingAddress(HttpSession session,Integer shippingId){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }
        return iShippingService.getShippingAddressDetail(user.getId(),shippingId);
    }

    /**
     * 列出收货地址接口
     * 有分页功能
     * @param session
     * @return
     */
    @RequestMapping(value = "list_shipping_address_detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<ShippingVO>> listShippingAddress(HttpSession session,
                                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                                    @RequestParam(defaultValue = "10") Integer pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }
        return  iShippingService.listShippingAddress(user.getId(),pageNum,pageSize);
    }
}
