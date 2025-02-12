package com.example.demo;

    import com.example.demo.model.ShortUrl;
    import com.example.demo.model.ShortUrlVO;
    import com.example.demo.service.ShortUrlService;
    import com.fasterxml.jackson.core.type.TypeReference;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;

    import java.io.*;
    import java.util.*;

    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertTrue;

    @SpringBootTest
    public class DemoApplicationTest {

        @Autowired
        private ShortUrlService shortUrlService;

        /**
         * 创建短链接
         * @param originalUrl 原始长链接
         * @return 创建的短链接信息
         */
        private ShortUrl createShortUrl(String originalUrl) {
            ShortUrlVO shortUrlVO = ShortUrlVO.builder()
                    .originalUrl(originalUrl)
                    .build();
            return shortUrlService.createShortUrl(shortUrlVO);
        }

        @BeforeEach
        public void setup() {
            shortUrlService.clearUrlMap();
        }

        /**
         * 生成测试用的URL列表
         * @throws Exception
         */
        public void generateUrls() throws Exception {
            List<String> urls = new ArrayList<>();
            for (int i = 1; i <= 106; i++) {
                urls.add("http://localhost:8080/article/detail/" + i);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(urls);
            System.out.println(json);
        }

        /**
         * 生成短链接并输出到CSV文件
         * @throws IOException
         */
        @Test
        public void generateUrlsAndOutputCsv() throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("originUrl.json")).getFile());
            List<String> originUrls = objectMapper.readValue(file, new TypeReference<List<String>>() {});

            List<String[]> csvData = new ArrayList<>();
            csvData.add(new String[]{"id", "originUrl", "shortUrl"});

            for (int i = 0; i < originUrls.size(); i++) {
                ShortUrl shortUrl = createShortUrl(originUrls.get(i));
                csvData.add(new String[]{String.valueOf(i + 1), shortUrl.getOriginalUrl(), shortUrl.getShortUrl()});
            }

            try (FileWriter csvWriter = new FileWriter("result.csv")) {
                for (String[] rowData : csvData) {
                    csvWriter.append(String.join(",", rowData));
                    csvWriter.append("\n");
                }
            }
        }

        /**
         * 测试CSV文件中的短链接
         * @throws IOException
         */
        @Test
        public void testCsvUrls() throws IOException {
            this.loadCsvDataToUrlMap();
            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("result.csv")).getFile());
            Map<String, String> csvData = new HashMap<>();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Skip header line
                    }
                    String[] values = line.split(",");
                    String originUrl = values[1];
                    String shortUrl = values[2];
                    csvData.put(originUrl, shortUrl);
                }
            }
            for (Map.Entry<String, String> entry : csvData.entrySet()) {
                String originUrl = entry.getKey();
                String shortUrl = entry.getValue();
                Optional<String> originalUrl = shortUrlService.redirectUrl(shortUrl);
                assertTrue(originalUrl.isPresent());
                assertEquals(originUrl, originalUrl.get());
            }
        }

        /**
         * 加载CSV文件中的短链接映射
         * @throws IOException
         */
        private void loadCsvDataToUrlMap() throws IOException {
            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("result.csv")).getFile());
            Map<String, String> csvData = new HashMap<>();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Skip header line
                    }
                    String[] values = line.split(",");
                    String originUrl = values[1];
                    String shortUrl = values[2];
                    csvData.put(originUrl, shortUrl);
                }
            }
            shortUrlService.loadUrlMap(csvData);
        }
    }