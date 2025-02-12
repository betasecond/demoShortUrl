package com.example.demo.controller;

    import com.example.demo.model.ShortUrl;
    import com.example.demo.model.ShortUrlVO;
    import com.example.demo.service.ShortUrlService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/sol")
    public class ShortUrlController {

        @Autowired
        private ShortUrlService shortUrlService;

        /**
         * 接收创建短链接的请求
         * @param shortUrlVO 包含原始长链接和用户信息
         * @return 创建的短链接信息
         */
        @PostMapping("/url")
        public ResponseEntity<ShortUrl> createShortUrl(@RequestBody ShortUrlVO shortUrlVO) {
            ShortUrl shortUrl = shortUrlService.createShortUrl(shortUrlVO);
            return ResponseEntity.ok(shortUrl);
        }

        /**
         * 根据短链接获取原始长链接
         * @param shortUrl 短链接
         * @return 原始长链接
         */
        @GetMapping("/{shortUrl}")
        public ResponseEntity<String> getOriginalUrl(@PathVariable String shortUrl) {
            return shortUrlService.redirectUrl(shortUrl)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        /**
         * 重定向到原始长链接
         * @param shortUrl 短链接
         * @return 重定向响应
         */
        @GetMapping("/redirect/{shortUrl}")
        public ResponseEntity<Object> redirectUrl(@PathVariable String shortUrl) {
            return shortUrlService.redirectUrl(shortUrl)
                    .map(url -> ResponseEntity.status(302).header("Location", url).build())
                    .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping("/redirect")
        public ResponseEntity<Object> redirectUrlPost(@RequestBody String shortUrl) {
            return shortUrlService.redirectUrl(shortUrl)
                    .map(url -> ResponseEntity.status(302).header("Location", url).build())
                    .orElse(ResponseEntity.notFound().build());
        }
    }