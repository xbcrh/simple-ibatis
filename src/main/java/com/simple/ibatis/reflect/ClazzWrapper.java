package com.simple.ibatis.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author xiabing
 * @Desc clazz解析类
 **/
public class ClazzWrapper {
    /**
     * 待解析类
     * */
    private Class<?> clazz;
    /**
     * 该类存储的属性名
     * */
    private Set<String> propertiesSet = new HashSet<>();

    /**
     * 该类存储的属性名及属性类
     * */
    private Set<FiledExpand> filedExpandSet = new HashSet<>();

    /**
     * 该类存储的get方法。key为属性名，value为getxxx方法
     * */
    private Map<String,Method> getterMethodMap = new HashMap<>();
    /**
     * 该类存储的set方法。key为属性名，value为setxxx方法
     * */
    private Map<String,Method> setterMethodMap = new HashMap<>();
    /**
     * 缓存，避免对同一个类多次解析
     * */
    private static Map<String,ClazzWrapper> clazzWrapperMap = new ConcurrentHashMap<>();

    public ClazzWrapper(Class clazz){
        this.clazz = clazz;
// 对类进行解析
        if(!clazzWrapperMap.containsKey(clazz.getName())){
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields){
                FiledExpand filedExpand = new FiledExpand(field.getName(),field.getType());
                filedExpandSet.add(filedExpand);
                propertiesSet.add(field.getName());
            }

            Method[] methods = clazz.getMethods();
            for(Method method:methods){
                String name = method.getName();
                if(name.startsWith("get")){
                    name = name.substring(3);
                    if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
                        name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
                    }
                    if(propertiesSet.contains(name)){
                        getterMethodMap.put(name,method);
                    }
                }else if(name.startsWith("set")){
                    name = name.substring(3);
                    if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
                        name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
                    }
                    if(propertiesSet.contains(name)){
                        setterMethodMap.put(name,method);
                    }
                }
                else {
                    continue;
                }
            }
            clazzWrapperMap.put(clazz.getName(),this);
        }
    }

    public boolean hasGetter(String properties){
        ClazzWrapper clazzWrapper = clazzWrapperMap.get(clazz.getName());

        return clazzWrapper.getterMethodMap.containsKey(properties);
    }

    public boolean hasSetter(String properties){
        ClazzWrapper clazzWrapper = clazzWrapperMap.get(clazz.getName());

        return clazzWrapper.setterMethodMap.containsKey(properties);
    }

    public Method getSetterMethod(String properties){
        if(!hasSetter(properties)){
            throw new RuntimeException("properties " + properties + " is not set method") ;
        }
        ClazzWrapper clazzWrapper = clazzWrapperMap.get(clazz.getName());
        return clazzWrapper.setterMethodMap.get(properties);
    }

    public Method getGetterMethod(String properties){
        if(!hasGetter(properties)){
            throw new RuntimeException("properties " + properties + " is not get method") ;
        }
        ClazzWrapper clazzWrapper = clazzWrapperMap.get(clazz.getName());
        return clazzWrapper.getterMethodMap.get(properties);
    }

    public Set<String> getProperties(){

        ClazzWrapper clazzWrapper = clazzWrapperMap.get(clazz.getName());

        return clazzWrapper.propertiesSet;
    }

    public Set<FiledExpand> getFiledExpandSet(){
        ClazzWrapper clazzWrapper = clazzWrapperMap.get(clazz.getName());

        return clazzWrapper.filedExpandSet;
    }

    public static class FiledExpand{

        String propertiesName;

        Class type;

        public FiledExpand() {
        }

        public FiledExpand(String propertiesName, Class type) {
            this.propertiesName = propertiesName;
            this.type = type;
        }

        public String getPropertiesName() {
            return propertiesName;
        }

        public void setPropertiesName(String propertiesName) {
            this.propertiesName = propertiesName;
        }

        public Class getType() {
            return type;
        }

        public void setType(Class type) {
            this.type = type;
        }

        @Override
        public int hashCode() {
            return propertiesName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof FiledExpand){
                return ((FiledExpand) obj).propertiesName.equals(propertiesName);
            }
            return false;
        }
    }
}