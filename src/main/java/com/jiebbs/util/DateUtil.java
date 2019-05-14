package com.jiebbs.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间处理工具类
 * @author weijie
 * @version 2019-05-14
 */
public class DateUtil {

    /**
     * 将传入的Date 转换为 String
     * @param date
     * @return
     */
    public static String date2str(Date date){
        if(null!=date){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(date);
        }
        return null;
    }
}
