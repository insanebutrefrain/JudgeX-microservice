# 使用Spring Cloud Alibaba将项目改造为 微服务

> 官网：[https://sca.aliyun.com/](https://sca.aliyun.com/ "Spring Cloud Alibaba")  
> 云原生脚手架：[https://start.aliyun.com/](https://start.aliyun.com/ "阿里云原生脚手架")

## 主要改造过程

### 1. 服务注册与发现（Nacos）

#### 1.1 安装并启动 Nacos Server

``` bash
# 下载 Nacos
wget https://github.com/alibaba/nacos/releases/download/2.2.0/nacos-server-2.2.0.tar.gz

# 解压并启动
tar -zxvf nacos-server-2.2.0.tar.gz
cd nacos/bin
sh startup.sh -m standalone
```

#### 1.2 配置应用连接到 Nacos

创建 `bootstrap.yml` 文件（优先级高于 application.yml）：

``` yaml
spring:
  application:
    name: your-service-name  # 服务名称
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # Nacos服务器地址
        namespace: public  # 命名空间，可选
        group: DEFAULT_GROUP  # 分组，可选
      config:
        server-addr: localhost:8848
        file-extension: yaml  # 配置文件扩展名
        namespace: public
        group: DEFAULT_GROUP
```

#### 1.3 启用服务发现

在主应用类上添加 `@EnableDiscoveryClient` 注解：

```java
@SpringBootApplication
@EnableDiscoveryClient
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 2.模块划分

#### 为了便于各模块之间的调用，一般将微服务后端划分为以下模块

* model 实体类模块
* common 公共模块
* gatway 网关模块（聚合调用接口）
* service-client 模块，提供各模块之间的相互调用的接口
* user-sevice 模块，业务模块
* 其他业务模块

### 3. 服务调用（OpenFeign）

#### 3.1 启用 Feign 客户端

在主应用类上添加 `@EnableFeignClients` 注解：

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class YourApplication {
    // ...
}
```

#### 3.2 创建 Feign 客户端接口

```java
@FeignClient(name = "user-service")  // 指定服务名称
public interface UserServiceClient {
    
    @GetMapping("/users/{id}")
    User getUserById(@PathVariable("id") Long id);
    
    @PostMapping("/users")
    User createUser(@RequestBody User user);
}
```

#### 3.3 使用 Feign 客户端

```java
@RestController
public class OrderController {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @GetMapping("/orders/{orderId}/user")
    public User getOrderUser(@PathVariable Long orderId) {
        // 通过Feign调用用户服务
        return userServiceClient.getUserById(orderId);
    }
}
```
