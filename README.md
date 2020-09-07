# simple-ibatis
## 手写mybatis框架---思路变成实践

## 项目简介：
simple-batis是自己编写的一个简单ORM框架。在学习mybatis源码时，有感而发。耗时3周左右，基本满足了一些常用的Sql操作本项目所涉及的代码都是个人所写，没有一句copy，肯定不是很完善，大家理解下，后续有时间会一直更新。如果你对源码感兴趣，也可以加入一起,将自己的理解转为代码真的会加深印象。
代码运行默认在java8上，因为用到了参数反射，所以在idea中记得开启parameters；

__开启步骤如下__
1.File->Settings->Build,Execution,Deployment->Compiler->Java Compiler
2.在 Additional command line parameters: 后面填上 -parameters

## 代码简介：

### 注释 com.simple.ibatis.annotation
__@Dao__
标注在mapper类上。标志着该类是一个mapper类，在解析时会进行解析。
```
@Dao
public interface App1 {
}
```
__@Select__
标注在mapper类中的方法上。标志着该方法是一个Select方法，并在Select方法内部写具体的sql语句。对于有参数注入的情况，参数使用{}进行代替。
```
@Select("SELECT name from sys_user where name = {user.name} and id = {id}")
List<String> test1(User user, int id);
```

__@Param__
标注在mapper类中的方法参数上。对参数名进行一次重命名。若不使用此注释，会默认按照参数名当做注入的元素。
```
@Select("SELECT name from sys_user where id = {userId}")
List<String> test2(@Param("userId") int id);
```

__@Update__
标注在mapper类中的方法上。标志着该方法是一个Update方法。
```
@Update("update sys_user set name = {user.name} where id = {user.id}")
void update3(User user);
```

__@Insert__
标注在mapper类中的方法上。标注着该方法是一个Insert方法
```
@Insert("insert into sys_user(id,name) values ({user.id},{user.name})")
int insert4(@Param("user") User user);
```

__@Delete__
标注在mapper类中的方法上。标注着该方法是一个Delete方法
```
@Delete("delete from sys_user where id = {user.id}")
int delete5(@Param("user") User user);
```

### 数据库注册 com.simple.ibatis.driver
DriverRegister 提供数据库注册功能。未避免重复注册，内部使用了一个缓存。

### 数据源 com.simple.ibatis.datasource
NormalDataSource 普通数据源，没有池化的功能，提供获取数据库连接的功能。
PoolDataSource 池化数据源，存放着活跃连接列表和空闲连接列表。并对获取连接和释放连接做了一系列操作。
PoolConnection 连接的包装类，除了存放真实连接外，还存放此连接被获取时间，用于判断连接是否超时。

### 核心类 com.simple.ibatis.core
Config 全局核心类，存放数据源，mapper包地址，mapper类解析文件
MapperCore mapper类解析文件
SqlSource 具体的sql语句封装

### 代理类 com.simple.ibatis.mapping
MapperProxy mapper接口代理类。使用动态代理技术

### 执行器类 com.simple.ibatis.execute
Executor 执行器接口
SimpleExecutor 具体执行器，执行具体的sql方法。生成结果
ExecutorFactory 生成Executor的工厂类

### 反射类 com.simple.ibatis.reflect
ClassWrapper 类加强器，封装了Object的get和set方法。
ObjectWrapper 对象包装类。调用ObjectWrapper.setVal和getVal就可以设置和获得属性。不需要显示的调用对象的getxxx和setxxx方法。
ObjectWrapperFactory 对象包装类生成器

### 处理器类 com.simple.ibatis.statement
PreparedStatementHandle PreparedStatement生成器。将java属性转为jdbc属性并注入。
ResultSetHandle 对查询结构ResultSet进行解析，转换为Java类型

### 工具类 com.simple.ibatis.util
PackageUti 解析包的工具类
TypeUtil 类型判断的工具类

### 缓存类 com.simple.ibatis.cache
Cache 缓存接口类
SimpleCache 简单基本缓存类
LruCache Lru淘汰策略缓存类

## 操作示例：

### 1. 构建pojo文件（并在数据库中建立该表）
```
public class User {
private int id;

private String name;

public int getId() {
return id;
}

public void setId(int id) {
this.id = id;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}
}
```

### 2. 构建mapper文件

```
package com.simple.ibatis.mapper;

@Dao
public interface App1 {

@Select("SELECT name from sys_user where name = {user.name} and id = {id}")
List<String> select3(User user, @Param("id") int id);

@Update("update sys_user set name = {user.name} where id = {user.id}")
void update4(User user);

@Insert("insert into sys_user(id,name) values ({user.id},{user.name})")
int insert5(@Param("user") User user);
}

```

### 3. 构建数据源和执行器工厂类：
```
PoolDataSource poolDataSource = new PoolDataSource("com.mysql.jdbc.Driver","jdbc:mysql://101.132.150.75:3306/our-auth","root","root");
Config config = new Config("com/simple/ibatis/mapper",poolDataSource);
config.setOpenCache(true);
```

### 4. 操作：
```
SimpleExecutor executor = config.getExecutor();
App1 app1 = executor.getMapper(App1.class);
User user = new User();
user.setName("xiabing");
user.setId(3);
int count = app1.insert5(user);
app1.update4(user);
List<String> users = app1. select3(user,3);
// 第二次从缓存获取
List<String> users1 = app1. select3(user,3);

System.out.println(users.get(0));
```