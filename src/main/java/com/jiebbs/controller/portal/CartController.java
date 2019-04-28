package com.jiebbs.controller.portal;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.User;
import com.jiebbs.service.ICartService;
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

        //查询产品是否



        return null;
    }
}
