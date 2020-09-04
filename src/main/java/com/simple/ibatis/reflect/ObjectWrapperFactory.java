package com.simple.ibatis.reflect;

/**
 * @Author xiabing
 * @Desc objectWrapper工厂类
 **/
public class ObjectWrapperFactory {

    public static ObjectWrapper getInstance(Object o){
        return new ObjectWrapper(o);
    }

}