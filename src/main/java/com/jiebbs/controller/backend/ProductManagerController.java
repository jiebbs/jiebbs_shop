package com.jiebbs.controller.backend;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Product;
import com.jiebbs.pojo.User;
import com.jiebbs.service.IProductService;
import com.jiebbs.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 产品模块接口
 * @author weijie
 * @version 1.0 2019-04-23
 */
@Controller
@RequestMapping(value = "/manage/product/")
public class ProductManagerController {

    @Resource(name="iUserService")
    private IUserService iUserService;

    @Resource(name="iProductService")
    private IProductService iProductService;

    /**
     * 保存商品接口
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "product_save.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> productSave(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要进行登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("用户非管理员，无权进行操作");
        }

        return iProductService.productSaveOrUpdate(product);
    }
}
