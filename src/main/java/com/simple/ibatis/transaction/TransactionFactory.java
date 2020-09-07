package com.simple.ibatis.transaction;

import com.simple.ibatis.datasource.PoolDataSource;
import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @Author xiabing
 * @Desc 事务工厂
 **/
public class TransactionFactory {

    public static SimpleTransaction newTransaction(PoolDataSource poolDataSource, Integer level, Boolean autoCommmit){
        return new SimpleTransaction(poolDataSource,level,autoCommmit);
    }

}