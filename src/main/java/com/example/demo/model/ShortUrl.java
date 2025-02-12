package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortUrl {

    private Long id;
    private String originalUrl;
    private String shortUrl;
    private String username = ""; // 用户名
    private String thirdPartyUserId = ""; // 第三方用户ID
    private String userAgent = ""; // 用户访问环境
    private String loginMethod = ""; // 用户登录方式

}