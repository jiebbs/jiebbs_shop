package com.jiebbs.service.impl;

import com.google.common.collect.Lists;
import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.dao.CartMapper;
import com.jiebbs.dao.ProductMapper;
import com.jiebbs.pojo.Cart;
import com.jiebbs.pojo.Product;
import com.jiebbs.service.ICartService;
import com.jiebbs.util.BigDecimalUtil;
import com.jiebbs.util.PropertiesUtil;
import com.jiebbs.vo.CartProductVO;
import com.jiebbs.vo.CartVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;


    public ServerResponse<CartVO> addProduct(Integer userId, Integer productId, Integer count){
        //产品参数校验
        if(null == productId || null == count){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //校验之前该用户是否有此产品
        Cart cart =  cartMapper.selectCartByUserIdProductId(userId,productId);
        //如果不存在该商品则创建该商品购物车
        int cartResult = 0;
        if(null==cart){
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setProductId(productId);
            newCart.setChecked(Const.CartProductStatus.CHECKED);
            newCart.setQuantity(count);
            cartResult = cartMapper.insert(newCart);
        }else{
            //若存在则在原来产品数量上增加添加的产品数量
            cart.setQuantity(cart.getQuantity()+count);
            cartResult = cartMapper.updateByPrimaryKeySelective(cart);
        }
        CartVO cartVO = assembleLimitCartVO(userId);
        return cartResult>0?ServerResponse.<CartVO>createBySuccessMessageAndData("产品添加购物车成功",cartVO)
                :ServerResponse.<CartVO>createByErrorMessage("产品添加购物车失败");
    }


    public ServerResponse<CartVO> updateProduct(Integer userId,Integer productId,Integer count,Integer checked){
        //产品参数校验
        if(null == productId || null == count || null == checked){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //校验之前该用户是否有此产品
        Cart cart =  cartMapper.selectCartByUserIdProductId(userId,productId);
        int cartResult = 0;
        if(null!=cart){
            Cart newCart = new Cart();
            newCart.setId(cart.getId());
            newCart.setQuantity(count);
            newCart.setChecked(checked);
            cartResult = cartMapper.updateByPrimaryKeySelective(newCart);
            if(cartResult>0){
                CartVO cartVO = assembleLimitCartVO(userId);
                return ServerResponse.createBySuccessMessageAndData("更新产品状态成功",cartVO);
            }
        }
        return ServerResponse.createByErrorMessage("更新产品状态失败");
    }


    public ServerResponse<CartVO> deleteProduct(Integer userId,String productIds){
        List<String> productIdList = Lists.newArrayList(productIds.split(","));
        //产品参数校验
        if(CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //TODO 这里可能要考量一下是否加入产品是否已经删除的校验（个人认为可加可不加）
        //删除
        int deleteResult = cartMapper.deleteCartByUserIdProductIds(userId,productIdList);
        if(deleteResult>0){
            CartVO cartVO = assembleLimitCartVO(userId);
            return ServerResponse.createBySuccessMessageAndData("删除产品成功",cartVO);
        }
        return ServerResponse.createByErrorMessage("删除产品失败");
    }


    public ServerResponse<CartVO> getCartList(Integer userId){
        CartVO cartVO = assembleLimitCartVO(userId);
        return ServerResponse.createBySuccessMessageAndData("获取购物车列表成功",cartVO);
    }

    public ServerResponse<Integer> countCartProduct(Integer userId){
        int count = cartMapper.selectCartProductCountByUserId(userId);
        return ServerResponse.createBySuccessData(count);
    }




    /**
     * 返回限制数量的购物车通用对象
     * @param userId
     * @return
     */
    private CartVO assembleLimitCartVO(Integer userId){
        CartVO cartVO = new CartVO();
        BigDecimal cartVOTotalPrice = new BigDecimal("0");
        cartVO.setCartTotalPrice(cartVOTotalPrice);
        //查询该userId下所有的购物车
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        //如果查询不到购物车返回一个null
        if(CollectionUtils.isNotEmpty(cartList)){
            List<CartProductVO> cartProductVOList = Lists.newArrayList();
            //循环获取每个购物车产品并且封装为通用的购物车产品对象
            for(Cart cart:cartList){
                CartProductVO cartProductVO = new CartProductVO();
                cartProductVO.setId(cart.getId());
                cartProductVO.setUserId(cart.getUserId());
                cartProductVO.setProductId(cart.getProductId());
                cartProductVO.setProductChecked(cart.getChecked());
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(null!=product){
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductStock(product.getStock());

                    //判断购物车数量和库存差距
                    int buyLimitQuantity = 0;
                    if(product.getStock()>=cart.getQuantity()){
                        cartProductVO.setLimitQuantity(Const.CartProductStatus.LIMIT_QUANTITY_SUCCESS);
                        buyLimitQuantity = cart.getQuantity();
                    }else{
                        cartProductVO.setLimitQuantity(Const.CartProductStatus.LIMIT_QUANTITY_FAIL);
                        buyLimitQuantity = product.getStock();
                        //更新购物车中的数量为最大库存
                        Cart updateStockCart = new Cart();
                        updateStockCart.setQuantity(buyLimitQuantity);
                        updateStockCart.setId(cart.getId());
                        cartMapper.updateByPrimaryKeySelective(updateStockCart);
                    }
                    cartProductVO.setQuantity(buyLimitQuantity);
                    //计算该产品总价
                    cartProductVO.setProductTotalPrice(BigDecimalUtil.multiply(product.getPrice(),buyLimitQuantity));
                }
                if(cartProductVO.getProductChecked()==Const.CartProductStatus.CHECKED){
                    //如果产品被勾选则将产品总价添加到购物车总价当中
                    cartVOTotalPrice = BigDecimalUtil.add(cartVOTotalPrice, cartProductVO.getProductTotalPrice());
                }
                //将封装后的对象放入到产品列表当中
                cartProductVOList.add(cartProductVO);
            }
            cartVO.setCartProductVOList(cartProductVOList);
            cartVO.setCartTotalPrice(cartVOTotalPrice);
            cartVO.setAllChecked(getAllCheckStatus(userId));
            cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }
        return cartVO;
    }

    /**
     * 判断购物车是否全选
     * @param userId
     * @return
     */
    private boolean getAllCheckStatus(Integer userId){
        if(null==userId){
            return false;
        }
        //若查询出一条未勾选的记录则返回false
        int result = cartMapper.selectCartCheckedStatusByUserId(userId);
        return result==0;
    }
}
