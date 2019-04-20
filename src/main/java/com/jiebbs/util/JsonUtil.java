package com.jiebbs.util;

import com.alibaba.fastjson.JSONObject;

/**
 * 个人封装的Json工具类
 * @author weijie
 * @version 1.0 2019-04-20
 */
public class JsonUtil {

    /**
     * 对象转换为JSON字符串
     * @param obj 需要转换为JSON的对象
     */
    public static void convert2JsonAndPrint(Object obj){
        String json = JSONObject.toJSONString(obj);
        System.out.println(json);
    }
}
