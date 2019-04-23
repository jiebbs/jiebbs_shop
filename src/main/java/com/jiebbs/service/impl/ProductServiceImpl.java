package com.jiebbs.service.impl;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.dao.ProductMapper;
import com.jiebbs.pojo.Product;
import com.jiebbs.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<String> productSaveOrUpdate(Product product){
        //校验参数
        if(null!=product){
            //获取产品主图(校验是否有上传子图)
            String subImges = product.getSubImages();
            if(StringUtils.isNotBlank(subImges)) {
                //与前端约定通过","分割
                String[] imageArray = subImges.split(",");
                //获取第一张子图作为主图
                product.setMainImage(imageArray[0]);
            }
            //判断商品是创建还是更新
            if(product.getId()!=null){
                int updateProductResult = productMapper.updateByPrimaryKeySelective(product);
                return updateProductResult>0?ServerResponse.<String>createBySuccessMessage("更新商品成功")
                        :ServerResponse.<String>createByErrorMessage("更新商品失败");
            }
            int createProductResult = productMapper.insertSelective(product);
            return createProductResult>0?ServerResponse.<String>createBySuccessMessage("创建商品成功")
                    :ServerResponse.<String>createByErrorMessage("创建商品失败");
        }

        return ServerResponse.createByErrorMessage("创建或更新产品失败,传入参数不能为空");
    }
}
