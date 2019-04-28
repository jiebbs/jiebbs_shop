package com.jiebbs.controller.backend;


import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Category;
import com.jiebbs.pojo.User;
import com.jiebbs.service.ICategoryService;
import com.jiebbs.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 后台商品分类接口
 * @author weijie
 * @version 1.0 2019-04-20
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManagerController {

    @Resource(name="iCategoryService")
    private ICategoryService iCategoryService;

    @Resource(name="iUserService")
    private IUserService iUserService;

    /**
     * 增加分类接口
     * 若前端没有传parentId，自动默认为0（根节点）
     * @param session
     * @param categoryName 分类名称
     * @param parentId 父分类的ID
     * @return
     */
    @RequestMapping(value="add_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, @RequestParam(value="categoryName")String categoryName,
                                              @RequestParam (value ="parentId",defaultValue = "0")Integer parentId){
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
        ServerResponse resp = iCategoryService.addCategory(categoryName,parentId);
        return resp;
    }

    /**
     * 更新分类名称接口
     * @param session
     * @param categoryId
     * @param newCategoryName
     * @return
     */
    @RequestMapping(value="set_category_name.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session,Integer categoryId,String newCategoryName){
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

        return iCategoryService.setCategoryName(categoryId,newCategoryName);
    }

    /**
     * 获取分类平级子节点接口（当没有categoryId时默认获取根节点下平级子节点）
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_child_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildParallelCategory(HttpSession session,
                 @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
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
        //非递归查询分类下的子分类，只查询当前分类下的平级分类
        ServerResponse resp = iCategoryService.getChildParallelCategory(categoryId);
        return resp;
    }

    /**
     * 递归获取商品分类节点
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_child_deep_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Integer>> getChildDeepCategory(HttpSession session,
                @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){

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

        //递归查询给定categoryId下的所有子节点
        return iCategoryService.getChildDeepCategory(categoryId);
    }
}
