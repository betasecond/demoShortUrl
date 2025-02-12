package com.example.demo;

import com.example.demo.model.ShortUrl;
import com.example.demo.model.ShortUrlDTO;
import com.example.demo.model.ShortUrlVO;
import com.example.demo.repository.ShortUrlRepository;
import com.example.demo.service.ShortUrlService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(ShortUrlService.class)
public class DemoApplicationTest {

    @Autowired
    private ShortUrlService shortUrlService;

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    private ShortUrl createShortUrl(String originalUrl) {
        ShortUrlVO shortUrlVO = ShortUrlVO.builder()
                .originalUrl(originalUrl)
                .build();
        return shortUrlService.createShortUrl(shortUrlVO);
    }

    @BeforeEach
    public void setup() {
        shortUrlRepository.deleteAll();
    }

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
        for (Map.Entry<String, String> entry : csvData.entrySet()) {
            ShortUrlVO shortUrlVo = ShortUrlVO.builder()
                    .originalUrl(entry.getKey())
                    .build();

            shortUrlService.createShortUrl(shortUrlVo);
        }
    }
}