package com.jiebbs.service.impl;

import com.jiebbs.common.Const;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.dao.CartMapper;
import com.jiebbs.pojo.Cart;
import com.jiebbs.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;


    public ServerResponse<String> addProduct(Integer userId, Integer productId, Integer count){
        //校验之前该用户是否有勾选此产品
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
            return cartResult>0?ServerResponse.<String>createBySuccessMessage("产品添加购物车成功")
                    :ServerResponse.<String>createByErrorMessage("产品添加购物车失败");
        }
        //若存在则在原来产品数量上增加添加的产品数量
        cart.setQuantity(cart.getQuantity()+count);
        cartResult = cartMapper.updateByPrimaryKeySelective(cart);
        return cartResult>0?ServerResponse.<String>createBySuccessMessage("产品添加购物车成功")
                :ServerResponse.<String>createByErrorMessage("产品添加购物车失败");
    }
}
