package com.jiebbs.vo;

import java.math.BigDecimal;

/**
 * 通用的购物车对象(结合产品和购物车的对象)
 * @author weijie
 * @version v1.0 2019-04-30
 */
public class CartVO {

    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    private BigDecimal productTotalPrice;
    private Integer productStock;
    private Integer productChecked;

}
