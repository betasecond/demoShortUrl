package com.example.demo.service;

import com.example.demo.model.ShortUrl;
import com.example.demo.model.ShortUrlDTO;
import com.example.demo.model.ShortUrlVO;
import com.example.demo.repository.ShortUrlRepository;
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
    private final ShortUrlRepository shortUrlRepository;

    @Value("${shorturl.domain.prefix}")
    private String domain;

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public ShortUrlService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    /**
     * 创建短链接
     * @param shortUrlVO 包含原始长链接和用户信息
     * @return 创建的短链接信息
     */
    public ShortUrl createShortUrl(ShortUrlVO shortUrlVO) {
        String shortUrl = generateShortUrl(shortUrlVO.getOriginalUrl());
        ShortUrlDTO shortUrlObj = new ShortUrlDTO();
        shortUrlObj.setOriginalUrl(shortUrlVO.getOriginalUrl());
        shortUrlObj.setShortUrl(shortUrl);
        shortUrlObj.setUsername(shortUrlVO.getUsername());
        shortUrlObj.setThirdPartyUserId(shortUrlVO.getThirdPartyUserId());
        shortUrlObj.setUserAgent(shortUrlVO.getUserAgent());
        shortUrlObj.setLoginMethod(shortUrlVO.getLoginMethod());
        shortUrlRepository.save(shortUrlObj);
        return ShortUrl.builder()
                .originalUrl(shortUrlVO.getOriginalUrl())
                .shortUrl(domain + "/sol/" + shortUrl)
                .username(shortUrlVO.getUsername())
                .thirdPartyUserId(shortUrlVO.getThirdPartyUserId())
                .userAgent(shortUrlVO.getUserAgent())
                .loginMethod(shortUrlVO.getLoginMethod()).
                build();
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
        while (shortUrlRepository.findByShortUrl(shortUrl).isPresent()) {
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
        return shortUrlRepository.findByShortUrl(shortUrl).map(ShortUrlDTO::getOriginalUrl);
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





}