package com.simple.ibatis.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author xiabing
 * @Desc 对象增强,封装了get,set方法
 **/
public class ObjectWrapper {

    private Object realObject;

    private ClazzWrapper clazzWrapper;

    public ObjectWrapper(Object realObject){
        this.realObject = realObject;
        this.clazzWrapper = new ClazzWrapper(realObject.getClass());
    }

    public Object getVal(String property) throws Exception{

        return clazzWrapper.getGetterMethod(property).invoke(realObject,null);
    }

    public void setVal(String property,Object value) throws Exception{

        clazzWrapper.getSetterMethod(property).invoke(realObject,value);
    }

    public Set<String> getProperties(){

        return clazzWrapper.getProperties();
    }

    public Set<ClazzWrapper.FiledExpand> getMapperFiledExpands(){

        return clazzWrapper.getFiledExpandSet();
    }

    public Object getRealObject(){

        return realObject;

    }
}
