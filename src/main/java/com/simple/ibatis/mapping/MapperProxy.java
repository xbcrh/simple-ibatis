package com.simple.ibatis.mapping;

import com.simple.ibatis.core.Config;
import com.simple.ibatis.execute.Executor;
import com.simple.ibatis.execute.SimpleExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @Author xiabing
 * @Desc
 **/
public class MapperProxy<T> implements InvocationHandler{

    private Class<T> interfaces;

    private SimpleExecutor executor;

    public MapperProxy(Class<T> interfaces,SimpleExecutor executor) {
        this.interfaces = interfaces;
        this.executor = executor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
//**object方法直接代理*/
        if(Object.class.equals(method.getDeclaringClass())){
            result = method.invoke(this,args);
        }else {
// getMethodType
            Integer methodType = executor.mapperCore.getMethodType(method);

            if(methodType == null){
                throw new RuntimeException("method is normal sql method");
            }
            if(methodType == 1){
/**返回集合类*/
                List<Object> list = executor.select(method,args);
                result = list;
                if(!executor.mapperCore.getHasSet(method)){
                    if(list.size() == 0){
                        result = null;
                    }else {
                        result = list.get(0);
                    }
                }

            }else{
                Integer count = executor.update(method,args);
                result = count;
            }
        }
        return result;
    }

    public T initialization(){
        return (T)Proxy.newProxyInstance(interfaces.getClassLoader(),new Class[] { interfaces },this);
    }
}