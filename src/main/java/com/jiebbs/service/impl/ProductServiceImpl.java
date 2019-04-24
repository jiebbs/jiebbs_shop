package com.jiebbs.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.dao.CategoryMapper;
import com.jiebbs.dao.ProductMapper;
import com.jiebbs.pojo.Product;
import com.jiebbs.service.IProductService;
import com.jiebbs.util.PropertiesUtil;
import com.jiebbs.vo.ProductDetailVO;
import com.jiebbs.vo.ProductListVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
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


    @Override
    public ServerResponse<String> setProductStatus(Integer productId, Integer status) {
        //校验参数
        if(null==productId||null==status||!Product.checkStatus(status)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int statusResult = productMapper.updateByPrimaryKeySelective(product);
        return statusResult>0?ServerResponse.<String>createBySuccessMessage("更新产品状态成功")
                :ServerResponse.<String>createByErrorMessage("更新产品状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVO> getProductDetail(Integer productId){
        //校验参数
        if(null==productId){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if(null==product){
            return ServerResponse.createByErrorMessage("产品已下架或者已删除");
        }
        //组装VO对象
        ProductDetailVO productDetailVO = this.assembleProductDetailVO(product);

        return ServerResponse.createBySuccessMessageAndData("获取产品详情成功",productDetailVO);
    }

    @Override
    public ServerResponse<PageInfo<ProductListVO>> getProducts(Integer pageNum, Integer pageSize){
        //使用pageHelper进行分页
        //初始化pageHelper(同时默认使用根据产品id的升序)
        PageHelper.startPage(pageNum,pageSize,"id asc");

        List<Product> productList = productMapper.getProductsByCategoryId();
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createBySuccessMessage("该商品分类下没有商品");
        }
        //转换集合中的product为productListVO
        List<ProductListVO> productListVOList = Lists.newArrayList();
        for(Product product:productList){
            productListVOList.add(assembleProductListVO(product));
        }
        PageInfo<ProductListVO> pageResult = new PageInfo<>(productListVOList);
        return ServerResponse.createBySuccessMessageAndData("查询产品列表成功",pageResult);
    }

    @Override
    public ServerResponse<PageInfo<ProductListVO>> searchProducts(Integer productId,String productName,Integer pageNum, Integer pageSize){
        //使用pageHelper进行分页
        //初始化pageHelper(同时默认使用根据产品id的升序)
        PageHelper.startPage(pageNum,pageSize,"id asc");

        List<Product> productList = productMapper.searchProductsByIdName(productId,productName);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createBySuccessMessage("查询不到匹配条件的商品");
        }
        //转换集合中的product为productListVO
        List<ProductListVO> productListVOList = Lists.newArrayList();
        for(Product product:productList){
            productListVOList.add(assembleProductListVO(product));
        }

        PageInfo<ProductListVO> pageResult = new PageInfo<>(productListVOList);
        return ServerResponse.createBySuccessMessageAndData("查询商品成功",pageResult);
    }



    //组装ProductDetailVO方法
    private ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setId(product.getId());
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setName(product.getName());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setCreateTime(product.getCreateTime());
        productDetailVO.setUpdateTime(product.getUpdateTime());
        //插入图片文件服务器地址
        String imageHost = PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.jiebbs.com/");
        productDetailVO.setImageHost(imageHost);
        //插入父分类ID
        Integer parentCategoryId = 0;
        if(product.getCategoryId()!=0){
            parentCategoryId = categoryMapper.selectParentCategoryId(product.getCategoryId());
        }
        productDetailVO.setParentCategoryId(parentCategoryId);
        return productDetailVO;
    }

    //组装ProductListVO对象
    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setName(product.getName());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setStatus(product.getStatus());
        productListVO.setPrice(product.getPrice());
        //插入图片文件服务器地址
        String imageHost = PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.jiebbs.com/");
        productListVO.setImageHost(imageHost);
        return productListVO;
    }
}