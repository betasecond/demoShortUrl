package com.example.demo.service;

import com.example.demo.model.ShortUrl;
import com.example.demo.model.ShortUrlVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ShortUrlService {
    private final Map<String, ShortUrl> urlMap = new HashMap<>();

    @Value("${shorturl.domain.prefix}")
    private String domain;

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 创建短链接
     * @param shortUrlVO 包含原始长链接和用户信息
     * @return 创建的短链接信息
     */
    public ShortUrl createShortUrl(ShortUrlVO shortUrlVO) {
        String shortUrl = generateShortUrl(shortUrlVO.getOriginalUrl());
        ShortUrl shortUrlObj = new ShortUrl();
        shortUrlObj.setOriginalUrl(shortUrlVO.getOriginalUrl());
        shortUrlObj.setShortUrl(domain + "/sol/" + shortUrl);
        shortUrlObj.setUsername(shortUrlVO.getUsername());
        shortUrlObj.setThirdPartyUserId(shortUrlVO.getThirdPartyUserId());
        shortUrlObj.setUserAgent(shortUrlVO.getUserAgent());
        shortUrlObj.setLoginMethod(shortUrlVO.getLoginMethod());
        urlMap.put(shortUrl, shortUrlObj);
        return shortUrlObj;
    }

    /**
     * 生成短链接
     * @param originalUrl 原始长链接
     * @return 生成的短链接
     */
    private String generateShortUrl(String originalUrl) {
        String hash = hashUrl(originalUrl);
        String shortUrl = base62Encode(hash);
        int extension = 0;

        // 检查哈希值是否碰撞
        while (urlMap.containsKey(shortUrl)) {
            extension++;
            shortUrl = base62Encode(hash + extension);
        }

        return shortUrl;
    }

    /**
     * 根据短链接重定向到原始长链接
     * @param shortUrl 短链接
     * @return 原始长链接
     */
    public Optional<String> redirectUrl(String shortUrl) {
        shortUrl = shortUrl.replace(domain + "/sol/", "");
        ShortUrl shortUrlObj = urlMap.get(shortUrl);
        log.debug("shortUrlObj: {}", shortUrlObj);
        return Optional.ofNullable(shortUrlObj).map(ShortUrl::getOriginalUrl);
    }

    /**
     * 对长链接生成哈希值
     * @param originalUrl 原始长链接
     * @return 哈希值
     */
    private String hashUrl(String originalUrl) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(originalUrl.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                hash.append(String.format("%02x", hashBytes[i]));
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将哈希值转换为Base62编码
     * @param input 哈希值
     * @return Base62编码
     */
    private String base62Encode(String input) {
        StringBuilder encoded = new StringBuilder();
        long value = Long.parseLong(input, 16);
        while (value > 0) {
            encoded.append(BASE62.charAt((int) (value % 62)));
            value /= 62;
        }
        return encoded.reverse().toString();
    }

    /**
     * 根据短链接获取原始长链接
     * @param shortUrl 短链接
     * @return 原始长链接
     */
    public Optional<ShortUrl> getOriginalUrl(String shortUrl) {
        return Optional.ofNullable(urlMap.get(shortUrl));
    }

    /**
     * 清空短链接映射
     */
    public void clearUrlMap() {
        urlMap.clear();
    }

    /**
     * 加载短链接映射
     * @param csvData 短链接映射数据
     */
    public void loadUrlMap(Map<String, String> csvData) {
        csvData.forEach((originUrl, shortUrl) -> {
            ShortUrl shortUrlObj = new ShortUrl();
            shortUrlObj.setOriginalUrl(originUrl);
            shortUrlObj.setShortUrl(shortUrl);
            urlMap.put(shortUrl.replace(domain + "/sol/", ""), shortUrlObj);
        });
    }
}