package com.jiebbs.controller.portal;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.User;
import com.jiebbs.service.ICartService;
import com.jiebbs.vo.CartVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 前端购物车模块
 * @author weijie
 * @version v1.0 2019-04-28
 */
@Controller
@RequestMapping("/cart/")
public class CartController {


    @Resource(name="iCartService")
    private ICartService iCartService;


    /**
     * 购物车添加产品接口
     * @param session
     * @param productId
     * @param productCount
     * @return
     */
    @RequestMapping(value = "add_product.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse add(HttpSession session, Integer productId, Integer productCount){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }

        return  iCartService.addProduct(user.getId(),productId,productCount);
    }

    /**
     * 更新购物车产品状态接口
     * @param session
     * @param productId
     * @param productCount
     * @param checked
     * @return
     */
    @RequestMapping(value = "update_product.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVO> update(HttpSession session, Integer productId, Integer productCount, Integer checked){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }
        return  iCartService.updateProduct(user.getId(),productId,productCount,checked);
    }

    /**
     * 根据传入的一个或多个产品ID删除产品
     * @param session
     * @param productIdArray
     * @return
     */
    @RequestMapping(value = "delete_product.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVO> delete(HttpSession session, Integer... productIds){
        //校验用户登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要进行登录");
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }
}
