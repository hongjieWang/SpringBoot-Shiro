# SpringBoot 集成 Shiro 

## 1、框架搭建

​	创建SpringBoot项目，集成Shiro组件。打开idea工具。进入File->New->Project选择Spring Initializr 

![image-20190303104116439](images\20190303104116439.png)

 	包命自己命名。项目创建好后，修改`pom.xml`文件，引入Shiro和Thymeleaf。pom.xml文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.3.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>cn.org.july.shiro</groupId>
	<artifactId>springboot-shiro</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>springboot-shiro</name>
	<description>Demo project for Spring Boot</description>

	<properties>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<!-- 2 引入thymeleaf依赖 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>

		<!-- 3 引入shiro-spring 依赖-->
		<dependency>
			<groupId>org.apache.shiro</groupId>
			<artifactId>shiro-spring</artifactId>
			<version>1.4.0</version>
		</dependency>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>

```

## 2、 新建模版文件

​		在`resources`目录下新建`templates`文件夹，在templates文件夹中新建test.html文件和user文件夹，在user文件夹中新建add.html和updtae.html.	项目结构如下：

![image-20190303105309038](images\20190303105309038.png)

​	`test.html` 内容如下：

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>测试Thymeleaf</title>
</head>
<body>
<h3 th:text="${test}"></h3>
<hr/>
进入用户添加页面：<a href="add">用户添加</a>
<br>
进入用户修改页面：<a href="update">用户修改</a>
</body>
</html>
```

​	`add.html`内容如下：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>用户添加页面</title>
</head>
<body>
<h1>用户添加页面</h1>
</body>
</html>
```

​	`update.html` 页面内容如下：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>用户修改页面</title>
</head>
<body>
<h1>用户修改页面</h1>
</body>
</html>
```

​	在项目中新增	`UserController` 包名自己起名即可。内容如下： 

```java
package cn.org.july.spring.shiro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @RequestMapping(value = "/hello")
    @ResponseBody
    public String hello() {
        return "ok";
    }
    @RequestMapping(value = "/testThymeleaf")
    public String testThymeleaf(Model model) {
        //把数据存入model
        model.addAttribute("test", "测试Thymeleaf");
        //返回test.html
        return "test";
    }

    @RequestMapping(value = "/add")
    public String add(Model model) {
        //把数据存入model
        model.addAttribute("test", "添加用户页面");
        //返回test.html
        return "/user/add";
    }
    
    @RequestMapping(value = "/update")
    public String update(Model model) {
        //把数据存入model
        model.addAttribute("test", "修改用户页面");
        //返回test.html
        return "/user/update";
    }
    
    @RequestMapping(value = "/toLogin")
    public String toLogin(){
        return "login";
    }
}

```



​	启动项目，访问`testThymeleaf`页面，在浏览器中输入`http://127.0.0.1:8080/testThymeleaf` ，点击两个连接。效果如下图：

![01](images\01.gif)



## 3、配置Shiro

​		在现有工程中新建两个包分别是`config`和`realm`两个包。在`realm`包中新建`UserRealm`类，该类实现用户认证和权限授权。内容先简单构建一下，

```java
public class UserRealm extends AuthorizingRealm {
    /**
     * 执行授权逻辑
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.printf("用户授权");
        return null;
    }

    /**
     * 执行认证逻辑
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.printf("用户认证");
        return null;
    }
}

```



​	在`config`包中新建`ShiroConfig`类。内容如下：

```java
/**
 * shiro 的配置类
 */
@Configuration
public class ShiroConfig {
    /**
     * 创建shiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        //配置Shiro过滤器
        /**
         * 内置Shiro过滤器实现相关拦截功能
         *      常用的过滤器有：
         *          anon  : 无需认证（登录）可以访问
         *          authc : 必须认证才能访问
         *          user  : 如果使用rememberMe的功能可以直接访问
         *          perms : 该资源必须得到资源访问权限才可以使用
         *          role  : 该资源必须得到角色授权才可以使用
         */
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/testThymeleaf", "anon");
        filterMap.put("/*", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        shiroFilterFactoryBean.setLoginUrl("/toLogin");
        return shiroFilterFactoryBean;
    }

    /**
     * 创建DefaultWebSecurityManager
     */
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 创建Realm
     */
    @Bean
    public UserRealm getRealm() {
        return new UserRealm();
    }

}

```

​	配置完成后，启动项目，访问测试页面，点击添加用户连接和修改用户连接。效果如下图。

![02](images\02.gif)

​	我们可以看到，当我们访问用户添加和用户修改时，会跳转到登录页面。我们配置的Shiro的访问规则如下：

```java
Map<String, String> filterMap = new LinkedHashMap<>();
filterMap.put("/testThymeleaf", "anon");// 不拦截测试页面
filterMap.put("/*", "authc"); //拦截所有请求
```

​	当我们点击用户添加和用户修改时，Shiro拦截了我们的请求，通过设置`shiroFilterFactoryBean.setLoginUrl("/toLogin");`将拦截后的请求重定向到`/toLogin`页面。

## 4、用户认证

### 1、完善login.html页面

​	新增form表单，实现用户名密码登录过程。完善后内容如下：

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>登录页面</title>
</head>
<body>
<h1>登录页面</h1>
<hr/>
<h3 th:text="${msg}" style="color: red"></h3>
<form method="post" action="login">
    用户名 ：<input type="text" name="username"><br>
    密 码 ：<input type="password" name="password"><br>
    <input type="submit" value="登录">
</form>
</body>
</html>
```

2、完善`UserController` 

 		新增`login`用户登录处理方法：

```java
@RequestMapping(value = "/login")
    public String login(String username, String password, Model model) {
        System.out.printf("username :" + username);
        //1、获取 Subject
        Subject subject = SecurityUtils.getSubject();
        //2、封装用户数据
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        //3、执行登录方法
        try {
            subject.login(usernamePasswordToken);
            return "redirect:/testThymeleaf";
        } catch (UnknownAccountException e) {//该异常用户名称不存在
            //登录失败，用户名称不存在
            model.addAttribute("msg", "用户名称不存在");
            return "login";
        } catch (IncorrectCredentialsException e) {//该异常密码错误
            //登录失败，密码错误
            model.addAttribute("msg", "密码错误");
            return "login";
        }
    }
```

3、修改`UserRealm` 完善用户认证逻辑。

```java
public class UserRealm extends AuthorizingRealm {
    /**
     * 执行授权逻辑
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.printf("用户授权");
        return null;
    }

    /**
     * 执行认证逻辑
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("用户认证开始");
        //1、模拟从数据库获取 用户名称和密码
        String usernameByDB = "admin";
        String passwordByDb = "admin";

        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        //2、判断用户名称是否存在
        if (!usernameByDB.equals(usernamePasswordToken.getUsername())) {
            //用户名称不存在，Shiro底层会抛出UnknowAccountException
            return null;
        }
        //3、判断密码是否正确
        return new SimpleAuthenticationInfo("", passwordByDb, "");
    }
}

```

​	注意：修改后检查ShiroConfig中过滤规则是否对／login 请求开放，如没有开放请求无法到达服务端。修改过滤规则。

```java
Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/testThymeleaf", "anon");
        filterMap.put("/login", "anon");
        filterMap.put("/*", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
```

​	修改完成后，重启服务。

![03](images\03.gif)

用户认证功能完成。

## 5、整合Mybatis 完成用户认证；

​	SpringBoot 整合Mybatis 建立数据库连接，完善DAO，service 层见[Spring Cloud项目搭建] https://blog.csdn.net/July_whj/article/details/85476857 博客说明，不在此处详细讲解。

​	修改`UserRealm` 将之前默认的用户名称密码切换到数据库。修改如下：

```java
public class UserRealm extends AuthorizingRealm {
	//注入UserService
    @Autowired
    private UserService userService;

    /**
     * 执行授权逻辑
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.printf("用户授权");
        return null;
    }

    /**
     * 执行认证逻辑
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("用户认证开始");

        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        //根据用户名称查看用户信息
        User user = userService.findUserByName(usernamePasswordToken.getUsername());
        //2、判断用户名称是否存在
        if (null == user || !user.getUserName().equals(usernamePasswordToken.getUsername())) {
            //用户名称不存在，Shiro底层会抛出UnknowAccountException
            return null;
        }
        //3、判断密码是否正确
        return new SimpleAuthenticationInfo("", user.getPassword(), "");
    }
}
```



效果如下：

​	![04](images\04.gif)



## 6、资源授权



### 1、新增资源访问权限配置

​	修改`ShiroConfig` 新增资源访问权限。

```java
/**
 * 授权资源
 */
        filterMap.put("/add", "perms[user:add]");//访问 ／add时需要user:add的权限
        filterMap.put("/*", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        shiroFilterFactoryBean.setUnauthorizedUrl("/noAuth");//如果没权限跳转到／noAuth请求
        shiroFilterFactoryBean.setLoginUrl("/toLogin");
```

### 2、新增`noAuth.html` 页面，如果没有权限访问，跳转至该页面。

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>授权失败</title>
</head>
<body>
<h1>没有权限，无法访问该页面。</h1>
</body>
</html>	
```



​	重启服务后访问如下：

![05](images\05.gif)

### 3、使用shiro标签控制前台显示控件

#### 	1、新增pom依赖，thymeleaf 扩展 shiro

```xml
<!-- thymeleaf 扩展 shiro-->
        <dependency>
            <groupId>com.github.theborakompanioni</groupId>
            <artifactId>thymeleaf-extras-shiro</artifactId>
            <version>2.0.0</version>
        </dependency>
```

​	2、修改`ShiroConfig` 配置

配置ShiroDialect, 用于thymeleaf和Shiro标签配置使用。新增如下配置：

```java
/**
     * 配置ShiroDialect, 用于thymeleaf和Shiro标签配置使用
     */
    @Bean
    public ShiroDialect getShiroDialect() {
        return new ShiroDialect();
    }
```



### 	3、修改test.html,新增Shiro标签

​		在test.html 中新增 `<div>`标签，并使用`shiro:hasPermission`该标签，判断当前登录用户是否有该权限。没有权限则隐藏该控件。

```html
<!DOCTYPE html> 
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml" xmlns:shiro="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>测试Thymeleaf</title>
</head>
<body>
<h3 th:text="${test}"></h3>
<hr/>
<div shiro:hasPermission="user:add">
    进入用户添加页面：<a href="add">用户添加</a>
</div>
<br>
<div shiro:hasPermission="user:update">
    进入用户修改页面：<a href="update">用户修改</a>
</div>
</body>
</html>
```



### 	4、修改`UserRealm`类，修改用户授权

​	修改授权类，获取当前登录用户权限，并赋值给Shiro。进行权限判定。获取当前登录用户通过`doGetAuthenticationInfo`方法，如果用户登录成功则将用户通过`SimpleAuthenticationInfo`对象赋值给`Subject`对象中`principal` 属性。我们可以通过以下方法获取当前登录用户信息,并将权限赋值给Shiro。

```java
/**
     * 执行授权逻辑
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.printf("用户授权");
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        simpleAuthorizationInfo.addStringPermission(user.getPerms());
        return simpleAuthorizationInfo;
    }
```

​	系统重启，访问浏览器，效果如下：

![06](images\06.gif)



附录：数据库脚本

```sql
/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : localhost
 Source Database       : cloudDB01

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : utf-8

 Date: 03/03/2019 21:24:28 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(50) DEFAULT NULL,
  `dbSource` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `perms` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Records of `user`
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES ('1', 'JULY', 'cloudDB01', '18232533234', 'july@163.com', '123456', 'user:add'), ('2', 'WHJ', 'cloudDB01', '12312312312', '123@qq.com', '123456', 'user:update');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

```



```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>用户修改页面</title>
</head>
<body>
<h1>用户修改页面</h1>
</body>
</html>
```

xin