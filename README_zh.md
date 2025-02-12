[中文](README_zh.md) | [English](README.md)

# 短链接服务演示

该项目是一个短链接生成系统的概念验证演示。它包括一个用于生成短链接的RESTful服务和用于验证实现的单元测试。

## 功能

- 用于生成短链接的REST API
- 使用`H2`进行内存存储
- 确保正确性的单元测试
- 在`application.properties`中配置域名前缀


## 项目结构

- `ShortUrlController.java`：处理短链接请求的REST控制器
- `ShortUrlService.java`：生成和管理短链接的服务层
- `ShortUrl.java`：存储短链接信息的数据结构
- `ShortUrlVO.java`：请求负载的数据结构
- `DemoApplicationTest.java`：短链接服务的单元测试
- `build.gradle.kts`：Gradle构建配置

## 配置

在`application.properties`中配置域名前缀：

```properties
shorturl.domain.prefix=http://yourdomain.com
```

## 使用方法

1. 克隆仓库。
2. 在`application.properties`中配置域名前缀。
3. 运行应用程序。
4. 使用REST API生成和检索短链接。

## REST API端点

- `POST /sol/url`：创建短链接
  - **请求体：**
    ```json
    {
      "originalUrl": "http://example.com",
      "username": "user123",
      "thirdPartyUserId": "tpUser123",
      "userAgent": "Mozilla/5.0",
      "loginMethod": "OAuth"
    }
    ```

- `POST /sol/original-url`：使用POST请求检索原始链接，短链接在请求体中
  - **请求体：**
    ```json
    {
      "shortUrl": "http://short.url/abc123"
    }
    ```

- `POST /sol/redirect`：使用POST请求重定向到原始链接，短链接在请求体中
  - **请求体：**
    ```json
    {
      "shortUrl": "http://short.url/abc123"
    }
    ```
## 单元测试

提供了单元测试来验证实现。测试使用`originUrl.json`和`result.csv`文件，这些文件可以使用`DemoApplicationTest`类生成。

## 未来计划

- 进一步的增强和优化

## 任务列表

- [x] 设置短链接生成服务
- [x] 实现REST API
- [x] 添加单元测试
- [x] 在`application.properties`中配置域名前缀
- [x] 集成H2SQL进行数据存储

## 运行应用程序

要运行应用程序，请使用以下命令：

```bash
./gradlew bootRun
```

## 运行测试

要运行单元测试，请使用以下命令：

```bash
./gradlew test
```

## 许可证

该项目使用MIT许可证。
