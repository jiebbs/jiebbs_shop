package com.jiebbs.controller.backend;


import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojo.Category;
import com.jiebbs.service.ICategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 商品分类接口
 * @author weijie
 * @version 1.0 2019-04-20
 */
@Controller
@RequestMapping("/manager/category/")
public class CategoryManagerController {

    @Resource(name="iCategoryService")
    private ICategoryService iCategoryService;

    /**
     * 增加商品分类接口
     * @param category
     * @return
     */
    @RequestMapping(value="",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addCategory(Category category){

        return null;
    }
}
