package com.simple.ibatis.statement;

import com.simple.ibatis.core.MapperCore;
import com.simple.ibatis.core.SqlSource;
import com.simple.ibatis.datasource.PoolDataSource;
import com.simple.ibatis.reflect.ObjectWrapper;
import com.simple.ibatis.reflect.ObjectWrapperFactory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author xiabing
 * @Desc PreparedStatement生成器
 **/
public class PreparedStatementHandle {
    /**
     * 全局核心mapper解析类
     */
    private MapperCore mapperCore;

    /**
     * 待执行的方法
     */
    private Method method;

    /**
     * 数据源
     */
    private PoolDataSource poolDataSource;

    /**
     * 连接
     */
    private Connection connection;

    /**
     * 方法输入参数
     */
    private Object[] args;

    public PreparedStatementHandle(MapperCore mapperCore, Method method, PoolDataSource poolDataSource, Object[] args) {
        this.mapperCore = mapperCore;
        this.method = method;
        this.poolDataSource = poolDataSource;
        this.args = args;
        connection = poolDataSource.getConnection();
    }

    /**
     * @Author xiabing
     * @Desc 参数处理核心方法 todo
     **/
    public PreparedStatement generateStatement() throws SQLException{

        SqlSource sqlSource = mapperCore.getStatement(method);
        PreparedStatement preparedStatement = connection.prepareStatement(sqlSource.getSql());

        Class<?>[] clazzes = mapperCore.getParameterType(method);
        List<String> paramNames = mapperCore.getParameterName(method);
        List<String> params = sqlSource.getParam();
        preparedStatement = typeInject(preparedStatement,clazzes,paramNames,params,args);
        return preparedStatement;
    }

    /**
     * @Author xiabing
     * @Desc preparedStatement构建
     * @Param preparedStatement 待构建的preparedStatement
     * @Param clazzes 该方法中参数类型数组
     * @Param paramNames 该方法中参数名称列表,若有@Param注解，则为此注解的值，默认为类名首字母小写
     * @Param params 待注入的参数名，如user.name或普通类型如name
     * @Param args 真实参数值
     **/
    private PreparedStatement typeInject(PreparedStatement preparedStatement,Class<?>[] clazzes,List<String> paramNames,List<String> params,Object[] args)throws SQLException{

        for(int i = 0; i < paramNames.size(); i++){
            String paramName = paramNames.get(i);
            Class type = clazzes[i];
            if(String.class.equals(type)){
                int injectIndex = params.indexOf(paramName);
/**此处是判断sql中是否有待注入的名称({name})和方法内输入对象名(name)相同，若相同，则直接注入*/
                if(injectIndex >= 0){
                    preparedStatement.setString(injectIndex+1,(String)args[i]);
                }
            }else if(Integer.class.equals(type) || int.class.equals(type)){
                int injectIndex = params.indexOf(paramName);
                if(injectIndex >= 0){
                    preparedStatement.setInt(injectIndex+1,(Integer)args[i]);
                }
            }else if(Float.class.equals(type) || float.class.equals(type)){
                int injectIndex = params.indexOf(paramName);
                if(injectIndex >= 0){
                    preparedStatement.setFloat(injectIndex+1,(Float)args[i]);
                }
            }else {
                ObjectWrapper objectWrapper = ObjectWrapperFactory.getInstance(args[i]);
                for(int j = 0; j < params.size(); j++){
/**此处是判断对象的属性 如user.name，需要先获取user对象，在调用getName方法获取值*/
                    if((params.get(j).indexOf(paramName)) >= 0 ){
                        try{
                            String paramProperties = params.get(j).substring(params.get(j).indexOf(".")+1);
                            Object object = objectWrapper.getVal(paramProperties);
                            Class childClazz = object.getClass();
                            if(String.class.equals(childClazz)){
                                preparedStatement.setString(j+1,(String)object);
                            }else if(Integer.class.equals(childClazz) || int.class.equals(childClazz)){
                                preparedStatement.setInt(j+1,(Integer)object);
                            }else if(Float.class.equals(childClazz) || float.class.equals(childClazz)){
                                preparedStatement.setFloat(j+1,(Float)object);
                            }else {
/**目前不支持对象中包含对象，如dept.user.name todo*/
                                throw new RuntimeException("now not support object contain object");
                            }
                        }catch (Exception e){
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                }
            }
        }
        return preparedStatement;
    }

    public void closeConnection(){

        poolDataSource.removeConnection(connection);
    }
}