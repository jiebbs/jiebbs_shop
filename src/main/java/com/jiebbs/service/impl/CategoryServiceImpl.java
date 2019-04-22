package com.jiebbs.service.impl;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.dao.CategoryMapper;
import com.jiebbs.pojo.Category;
import com.jiebbs.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public ServerResponse<String> addCategory(String catetoryName, Integer parentId) {
        //校验参数
        if(parentId==null|| StringUtils.isBlank(catetoryName)){
            return ServerResponse.createByErrorMessage("参数传递错误，请检查传入参数是否正确");
        }
        Category category = new Category();
        category.setName(catetoryName);
        category.setParentId(parentId);
        category.setStatus(true); //默认创建的分类是可用的

        int categoryResult = categoryMapper.insertSelective(category);
        return categoryResult>0?ServerResponse.<String>createBySuccessMessage("创建分类成功")
                :ServerResponse.<String>createByErrorMessage("创建分类失败");
    }

    @Override
    public ServerResponse<String> setCategoryName(Integer categoryId, String newCategoryName) {
        if(categoryId == null||StringUtils.isBlank(newCategoryName)){
            return ServerResponse.createByErrorMessage("参数传递错误，请检查传入参数是否正确");
        }
        //校验分类是否存在
        int categoryResult = categoryMapper.checkCategory(categoryId);
        if(categoryResult>0){
            //创建更新中间对象
            Category category = new Category();
            category.setId(categoryId);
            category.setName(newCategoryName);
            int updateResult = categoryMapper.updateByPrimaryKeySelective(category);
            return updateResult>0?ServerResponse.<String>createBySuccessMessage("品类名称更新成功")
                    :ServerResponse.<String>createByErrorMessage("更新品类失败");
        }
        return ServerResponse.createByErrorMessage("品类不存在或已被删除");
    }

    @Override
    public ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId){
        if(categoryId==null){
            return ServerResponse.createByErrorMessage("参数传递错误，请检查参数是否正确");
        }
        //校验分类是否存在(根节点为0需要特殊处理)
        int categoryResult =  categoryId == 0?1:categoryMapper.checkCategory(categoryId);
        if(categoryResult>0){
            List<Category> categoryList = categoryMapper.selectChildParallelCategoryByParentId(categoryId);
            if(CollectionUtils.isEmpty(categoryList)){
                logger.info("没有查到categoryId:"+categoryId+"的子节点分类");
            }
            return ServerResponse.createBySuccessMessageAndData("获取平级子节点成功",categoryList);
        }
        return ServerResponse.createByErrorMessage("品类不存在或已被删除");
    }

}
