package com.simple.ibatis.util;

/**
 * @Author xiabing
 * @Desc 类型处理
 **/
public class TypeUtil {

    public static boolean isBaseType(Class clazz){
        boolean flag = false;
        if(String.class.equals(clazz)){
            flag = true;
        }else if(Byte.class.equals(clazz) || byte.class.equals(clazz)){
            flag = true;
        }else if(Short.class.equals(clazz) || short.class.equals(clazz)){
            flag = true;
        }else if(Integer.class.equals(clazz) || int.class.equals(clazz)){
            flag = true;
        }else if(Long.class.equals(clazz) || long.class.equals(clazz)){
            flag = true;
        }else if(Float.class.equals(clazz) || float.class.equals(clazz)){
            flag = true;
        }else if(Double.class.equals(clazz) || double.class.equals(clazz)){
            flag = true;
        }else if(Boolean.class.equals(clazz) || boolean.class.equals(clazz)){
            flag = true;
        }else if(Character.class.equals(clazz) || char.class.equals(clazz)){
            flag = true;
        }
        return flag;
    }
}