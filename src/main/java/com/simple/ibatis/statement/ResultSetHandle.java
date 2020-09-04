package com.simple.ibatis.statement;

import com.simple.ibatis.reflect.ClazzWrapper;
import com.simple.ibatis.reflect.ObjectWrapper;
import com.simple.ibatis.reflect.ObjectWrapperFactory;
import com.simple.ibatis.util.TypeUtil;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author xiabing
 * @Desc ResultSet结果处理器
 **/
public class ResultSetHandle {

    /**转换的目标类型*/
    Class<?> typeReturn;

    /**待转换的ResultSet*/
    ResultSet resultSet;

    Boolean hasSet;

    public ResultSetHandle(Class<?> typeReturn,ResultSet resultSet){
        this.resultSet = resultSet;
        this.typeReturn = typeReturn;
    }

    /**
     * ResultSet处理方法，目前仅支持String,int,Float,不支持属性是集合类 todo
     * */
    public <T> List<T> handle() throws Exception{

        List<T> res = new ArrayList<>(resultSet.getRow());
        Object object = null;
        ObjectWrapper objectWrapper = null;
        Set<ClazzWrapper.FiledExpand> filedExpands = null;
        if(!TypeUtil.isBaseType(typeReturn)){
            object = generateObj(typeReturn);
            objectWrapper = ObjectWrapperFactory.getInstance(object);

/** 获取对象属性 */
            filedExpands = objectWrapper.getMapperFiledExpands();
        }

        while (resultSet.next()){
/** 若返回是基础数据类型 */
            if(String.class.equals(typeReturn)){
                String val = resultSet.getString(1);
                if(val != null){
                    res.add((T)val);
                }
            }else if(Integer.class.equals(typeReturn) || int.class.equals(typeReturn)){
                Integer val = resultSet.getInt(1);
                if(val != null){
                    res.add((T)val);
                }
            }else if(Float.class.equals(typeReturn) || float.class.equals(typeReturn)){
                Float val = resultSet.getFloat(1);
                if(val != null){
                    res.add((T)val);
                }
            }else {
                for(ClazzWrapper.FiledExpand filedExpand:filedExpands){
                    if(String.class.equals(filedExpand.getType())){
                        String val = resultSet.getString(filedExpand.getPropertiesName());
                        if(val != null){
                            objectWrapper.setVal(filedExpand.getPropertiesName(),val);
                        }
                    }else if(Integer.class.equals(filedExpand.getType()) || int.class.equals(filedExpand.getType())){
                        Integer val = resultSet.getInt(filedExpand.getPropertiesName());
                        if(val != null){
                            objectWrapper.setVal(filedExpand.getPropertiesName(),val);
                        }
                    }else if(Float.class.equals(filedExpand.getType()) || float.class.equals(filedExpand.getType())){
                        Float val = resultSet.getFloat(filedExpand.getPropertiesName());
                        if(val != null){
                            objectWrapper.setVal(filedExpand.getPropertiesName(),val);
                        }
                    }else {
                        continue;
                    }
                }
                res.add((T)objectWrapper.getRealObject());
            }
        }
        return res;
    }

    private Object generateObj(Class<?> clazz) throws Exception{
        Constructor[] constructors = clazz.getConstructors();
        Constructor usedConstructor = null;
        for(Constructor constructor:constructors){
            if(constructor.getParameterCount() == 0){
                usedConstructor = constructor;
                break;
            }
        }
        if(constructors == null) {
            throw new RuntimeException(typeReturn + " is not empty constructor");
        }
        return usedConstructor.newInstance();
    }
}