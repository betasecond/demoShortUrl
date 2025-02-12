package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortUrlVO {
    private String originalUrl;
    @Builder.Default
    private String username = "defaultUsername";
    @Builder.Default
    private String thirdPartyUserId = "defaultThirdPartyUserId";
    @Builder.Default
    private String userAgent = "defaultUserAgent";
    @Builder.Default
    private String loginMethod = "defaultUserAgentLoginMethod";
}