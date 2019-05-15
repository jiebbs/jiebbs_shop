package com.jiebbs.controller.backend;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.User;
import com.jiebbs.service.IOrderService;
import com.jiebbs.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 后台订单模块
 * @author weijie
 * @version v1.0 2019-05-15
 */
@Controller
@RequestMapping("/manage/order/")
public class OrderManagerController {

    @Resource(name = "iOrderService")
    private IOrderService iOrderService;

    @Resource(name = "iUserService")
    private IUserService iUserService;

    /**
     * 获取订单接口
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("get_order_list.do")
    @ResponseBody
    public ServerResponse getOrderList(HttpSession session,
                                       @RequestParam(value="pageNum",defaultValue = "1")Integer pageNum,
                                       @RequestParam(value="pageSize",defaultValue = "10")Integer pageSize){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }
        //判断用户是否有管理员权限
        ServerResponse roleValid = iUserService.checkAdminRole(user);
        if(!roleValid.isSuccess()){
            return roleValid;
        }

        return iOrderService.getOrderList(null,pageNum,pageSize);
    }

    /**
     * 后台订单详情接口
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "get_order_detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getOrderDetail(HttpSession session,Long orderNo){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }
        //判断用户是否有管理员权限
        ServerResponse roleValid = iUserService.checkAdminRole(user);
        if(!roleValid.isSuccess()){
            return roleValid;
        }
        return iOrderService.getBackendOrderDetail(orderNo);
    }


    @RequestMapping(value = "search_order.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse searchOrder(HttpSession session,Long orderNo){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }
        //判断用户是否有管理员权限
        ServerResponse roleValid = iUserService.checkAdminRole(user);
        if(!roleValid.isSuccess()){
            return roleValid;
        }
        return iOrderService.getBackendOrderDetail(orderNo);
    }


    @RequestMapping(value = "send_goods.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse sendGoods(HttpSession session,Long orderNo){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }
        //判断用户是否有管理员权限
        ServerResponse roleValid = iUserService.checkAdminRole(user);
        if(!roleValid.isSuccess()){
            return roleValid;
        }
        return iOrderService.sendGoods(orderNo);
    }
}
