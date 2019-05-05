package com.jiebbs.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 个人封装的BigDecimal商业运输工具类
 * @author weijie
 * @version v1.0 2019-05-05
 */
public class BigDecimalUtil{

    private  BigDecimalUtil(){}

    /**
     * 相加方法
     * @param num1
     * @param num2
     * @return
     */
    public static BigDecimal add(Object num1,Object num2){
        //当参数都通过验证时才返回结果否则为null
        if(vaild(num1)&&vaild(num2)){
            BigDecimal toCovertNum1 = allType2StringBigDecimal(num1);
            BigDecimal toCovertNum2 = allType2StringBigDecimal(num2);
            return toCovertNum1.add(toCovertNum2);
        }
        return null;
    }

    /**
     * 相减方法
     * @param num1
     * @param num2
     * @return
     */
    public static BigDecimal substruct(Object num1,Object num2){
        if(vaild(num1)&&vaild(num2)){
            BigDecimal toCovertNum1 = allType2StringBigDecimal(num1);
            BigDecimal toCovertNum2 = allType2StringBigDecimal(num2);
            return toCovertNum1.subtract(toCovertNum2);
        }
        return null;
    }

    /**
     * 相乘方法
     * @param num1
     * @param num2
     * @return
     */
    public static BigDecimal multiply(Object num1,Object num2){
        if(vaild(num1)&&vaild(num2)){
            BigDecimal toCovertNum1 = allType2StringBigDecimal(num1);
            BigDecimal toCovertNum2 = allType2StringBigDecimal(num2);
            return toCovertNum1.multiply(toCovertNum2);
        }
        return null;
    }

    /**
     * 相除方法
     * 保留2位小数，四舍五入
     * @param num1
     * @param num2
     * @return
     */
    public static BigDecimal divide(Object num1,Object num2){
        if(vaild(num1)&&vaild(num2)){
            BigDecimal toCovertNum1 = allType2StringBigDecimal(num1);
            BigDecimal toCovertNum2 = allType2StringBigDecimal(num2);
            return toCovertNum1.divide(toCovertNum2,2,BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    /**
     * 验证参数和返回值方法
     * @return
     */
    private static Boolean vaild(Object num){
        if(num instanceof BigDecimal){
            return true;
        }
        BigDecimal bigDecimal = allType2StringBigDecimal(num);
        if(null==bigDecimal){
            return false;
        }
        return true;
    }

    /**
     * 根据传入的对象类型，调用自身相应的toString（），并调用BigDecimal的str构造器转换为BigDecimal
     * @param num
     * @return
     */
    private static BigDecimal allType2StringBigDecimal(Object num) {
        String result = "";
        if(num instanceof BigDecimal){
            return (BigDecimal) num;
        }else if(num instanceof Integer){ //判断是否为整型
            result = Integer.toString((Integer) num);
        }else if(num instanceof Double){ //判断是否为双精度类型
            result = Double.toString((Double) num);
        }else if(num instanceof Float){
            result = Float.toString((Float)num);
        }else if(num instanceof Long){
            result = Long.toString((Long)num);
        }
        if(StringUtils.isNotBlank(result)){
            return new BigDecimal(result);
        }
        return null;
    }
}
