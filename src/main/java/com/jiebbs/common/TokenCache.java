package com.jiebbs.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * 使用guava缓存对token进行缓存
 * @author weijie
 * @version 1.0 2019-04-19
 */
public class TokenCache {

    private static Logger logging = LoggerFactory.getLogger(TokenCache.class);

    public static String TOKEN_PREFIX = "token_";

    //声明本地缓存块
    private static LoadingCache localCache = CacheBuilder.newBuilder()
            .initialCapacity(1000) //声明初始缓存
            .maximumSize(10000)    //声明最大缓存
            .expireAfterAccess(12, TimeUnit.HOURS) //声明缓存有效期
            .build(new CacheLoader() {
                //默认的数据加载实现，当调用get获取对应key数据的时候，如果没有这个key对应数据，则调用此方法
                //默认返回null的字符串表示
                @Override
                public Object load(Object key) throws Exception {
                    return "null";
                }
            });

    public static void setKeyValue(String key,String value){
        localCache.put(key,value);
    }

    public static String getKeyValue(Object key) {
        String value = null;
        try{
            value = (String)localCache.get(key);
            if(StringUtils.equals("null",value)){
                return null;
            }
        }catch(Exception e){
            logging.error("guava缓存出错,获取token失败",e);
        }
        return value;
    }
}
