package com.jiebbs.controller.backend;

import com.github.pagehelper.PageInfo;
import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Product;
import com.jiebbs.pojo.User;
import com.jiebbs.service.IProductService;
import com.jiebbs.service.IUserService;
import com.jiebbs.vo.ProductDetailVO;
import com.jiebbs.vo.ProductListVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 更新产品上下架接口
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "set_product_status.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setSaleStatus(HttpSession session,Integer productId,Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要进行登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("用户非管理员，无权进行操作");
        }

        return iProductService.setProductStatus(productId,status);
    }

    /**
     * 获取产品详情接口
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping(value = "get_product_detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVO> getProductDetail(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要进行登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("用户非管理员，无权进行操作");
        }

        return iProductService.getProductDetail(productId);
    }

    /**
     * 获取产品列表接口(分页)
     * 不传入分页参数，默认pageNum为1，pageSize为10
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     */
    @RequestMapping(value = "get_product_list.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<ProductListVO>> getProductList(HttpSession session,
                                                                  @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要进行登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("用户非管理员，无权进行操作");
        }
        return iProductService.getProducts(pageNum,pageSize);
    }

    /**
     * 商品搜索接口
     * @param session
     * @param productId
     * @param productName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search_products.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<ProductListVO>> searchProducts(HttpSession session,
                                                             @RequestParam(value = "productId",required = false)Integer productId,
                                                             @RequestParam(value = "productName",required = false)String productName,
                                                             @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                             @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null==user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要进行登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("用户非管理员，无权进行操作");
        }
        //当传入的商品id和商品名称都为null时,默认返回全部商品信息
        if(null==productId&&null==productName){
            return iProductService.getProducts(pageNum,pageSize);
        }
        return iProductService.searchProducts(productId,productName,pageNum,pageSize);
    }
}
