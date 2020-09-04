package com.simple.ibatis.core;

import com.simple.ibatis.annotation.*;
import com.simple.ibatis.util.PackageUtil;
import com.sun.deploy.util.ArrayUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author xiabing
 * @Desc mapper方法解析核心类
 **/
public class MapperCore {

    private static final Integer SELECT_TYPE = 1;

    private static final Integer UPDATE_TYPE = 2;

    private static final Integer DELETE_TYPE = 3;

    private static final Integer INSERT_TYPE = 4;

    /**mapper文件解析类缓存*/
    private static Map<String,MethodDetails> cacheMethodDetails = new ConcurrentHashMap<>();

    /**
     * 全局配置
     * */
    private Config config;

    public MapperCore(Config config){
        this.config = config;
        load(config.getDaoSource());
    }

    /*
     * 预加载
     * */
    private void load(String source){
/**加载mapper包下的文件*/
        List<String> clazzNames = PackageUtil.getClazzName(source,true);
        try{
            for(String clazz: clazzNames){
                Class<?> nowClazz = java.lang.Class.forName(clazz);
// 不是接口跳过
                if(!nowClazz.isInterface()){
                    continue;
                }

/**接口上没有@Dao跳过 */
                boolean skip = false;
                Annotation[] annotations = nowClazz.getDeclaredAnnotations();
                for(Annotation annotation:annotations){
                    if(annotation instanceof Dao) {
                        skip = true;
                        break;
                    }
                }
                if(!skip){
                    continue;
                }

                Method[] methods = nowClazz.getDeclaredMethods();
                for( Method method : methods){
                    MethodDetails methodDetails = handleParameter(method);
                    methodDetails.setSqlSource(handleAnnotation(method));
                    cacheMethodDetails.put(generateStatementId(method),methodDetails);
                }
            }
        }catch (ClassNotFoundException e){
            throw new RuntimeException(" class load error,class is not exist");
        }
    }

    /**
     * 获得方法详情
     * */
    public MethodDetails getMethodDetails(Method method){
        String statementId = generateStatementId(method);
        if(cacheMethodDetails.containsKey(statementId)){
            return cacheMethodDetails.get(statementId);
        }
        return new MethodDetails();
    }

    /**
     * 获得方法对应的sql语句
     * */
    public SqlSource getStatement(Method method){
        String statementId = generateStatementId(method);
        if(cacheMethodDetails.containsKey(statementId)){
            return cacheMethodDetails.get(statementId).getSqlSource();
        }
        throw new RuntimeException(method + " is not sql");
    }

    /**
     * 获得方法对应的参数名
     * */
    public List<String> getParameterName(Method method){
        String statementId = generateStatementId(method);
        if(cacheMethodDetails.containsKey(statementId)){
            return cacheMethodDetails.get(statementId).getParameterNames();
        }
        return new ArrayList<>();
    }

    /**
     * 获取方法返回类型
     * */
    public Class getReturnType(Method method){
        String statementId = generateStatementId(method);
        if(cacheMethodDetails.containsKey(statementId)){
            return cacheMethodDetails.get(statementId).getReturnType();
        }
        return null;
    }

    /**
     * 获得方法对应的参数类型
     * */
    public Class<?>[] getParameterType(Method method) {
        String statementId = generateStatementId(method);
        if(cacheMethodDetails.containsKey(statementId)){
            return cacheMethodDetails.get(statementId).getParameterTypes();
        }
        return new Class<?>[]{};
    }

    /**
     * 获得方法是SELECT UPDATE DELETE INSERT
     * */
    public Integer getMethodType(Method method){
        String statementId = generateStatementId(method);
        if(cacheMethodDetails.containsKey(statementId)){
            return cacheMethodDetails.get(statementId).getSqlSource().getExecuteType();
        }
        return null;
    }

    /**
     * 获得方法是否返回集合类型list
     * */
    public boolean getHasSet(Method method){
        String statementId = generateStatementId(method);
        if(cacheMethodDetails.containsKey(statementId)){
            return cacheMethodDetails.get(statementId).isHasSet();
        }
        return false;
    }

    /**
     * 解析方法内的注解
     * */
    private MethodDetails handleParameter(Method method){

        MethodDetails methodDetails = new MethodDetails();

        int parameterCount = method.getParameterCount();

        Class<?>[] parameterTypes = method.getParameterTypes();

        List<String> parameterNames = new ArrayList<>();

        Parameter[] params = method.getParameters();
        for(Parameter parameter:params){
            parameterNames.add(parameter.getName());
        }

        /*
         * 获得方法参数的注解值替代默认值
         * */
        for(int i = 0; i < parameterCount; i++){
            parameterNames.set(i,getParamNameFromAnnotation(method,i,parameterNames.get(i)));
        }

        methodDetails.setParameterTypes(parameterTypes);
        methodDetails.setParameterNames(parameterNames);

/** 获取方法返回类型*/
        Type methodReturnType = method.getGenericReturnType();
        Class<?> methodReturnClass = method.getReturnType();
        if(methodReturnType instanceof ParameterizedType){
/** 返回是集合类 目前仅支持List todo*/
            if(!List.class.equals(methodReturnClass)){
                throw new RuntimeException("now ibatis only support list");
            }
            Type type = ((ParameterizedType) methodReturnType).getActualTypeArguments()[0];
            methodDetails.setReturnType((Class<?>) type);
            methodDetails.setHasSet(true);
        }else {
            methodDetails.setReturnType(methodReturnClass);
            methodDetails.setHasSet(false);
        }

        return methodDetails;
    }

    /**
     * 解析@select，@update注解
     * */
    private SqlSource handleAnnotation(Method method){
        SqlSource sqlSource = null;
        String sql = null;
        Annotation[] annotations = method.getDeclaredAnnotations();
        for(Annotation annotation : annotations){
            if(Select.class.isInstance(annotation)){
                Select selectAnnotation = (Select)annotation;
                sql = selectAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(SELECT_TYPE);
                break;
            }else if(Update.class.isInstance(annotation)){
                Update updateAnnotation = (Update)annotation;
                sql = updateAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(UPDATE_TYPE);
                break;
            }else if(Delete.class.isInstance(annotation)){
                Delete deleteAnnotation = (Delete) annotation;
                sql = deleteAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(DELETE_TYPE);
                break;
            }else if(Insert.class.isInstance(annotation)){
                Insert insertAnnotation = (Insert) annotation;
                sql = insertAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(INSERT_TYPE);
                break;
            }
        }
        if(sqlSource == null){
            throw new RuntimeException("method annotation not null");
        }
        return sqlSource;
    }

    /**
     * 获取@Param注解内容
     * */
    private String getParamNameFromAnnotation(Method method, int i, String paramName) {
        final Object[] paramAnnos = method.getParameterAnnotations()[i];
        for (Object paramAnno : paramAnnos) {
            if (paramAnno instanceof Param) {
                paramName = ((Param) paramAnno).value();
            }
        }
        return paramName;
    }

    /**
     * 生成唯一的statementId
     * */
    private static String generateStatementId(Method method){
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    public static class MethodDetails{
        /**方法返回类型,若是集合，则代表集合的对象类，目前集合类仅支持返回List */
        private Class<?> returnType;

        /**方法返回类型是否是集合*/
        private boolean HasSet;

        /**执行类型，SELECT,UPDATE,DELETE,INSERT*/
        private Integer executeType;

        /**方法输入参数类型集合*/
        private Class<?>[] parameterTypes;

        /**方法输入参数名集合*/
        private List<String> parameterNames;

        /**sql语句集合*/
        private SqlSource sqlSource;

        public Class<?> getReturnType() {
            return returnType;
        }

        public void setReturnType(Class<?> returnType) {
            this.returnType = returnType;
        }

        public boolean isHasSet() {
            return HasSet;
        }

        public void setHasSet(boolean hasSet) {
            HasSet = hasSet;
        }

        public Integer getExecuteType() {
            return executeType;
        }

        public void setExecuteType(Integer executeType) {
            this.executeType = executeType;
        }

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }

        public void setParameterTypes(Class<?>[] parameterTypes) {
            this.parameterTypes = parameterTypes;
        }

        public List<String> getParameterNames() {
            return parameterNames;
        }

        public void setParameterNames(List<String> parameterNames) {
            this.parameterNames = parameterNames;
        }

        public SqlSource getSqlSource() {
            return sqlSource;
        }

        public void setSqlSource(SqlSource sqlSource) {
            this.sqlSource = sqlSource;
        }
    }
}
