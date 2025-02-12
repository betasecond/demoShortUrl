package com.example.demo.repository;

import com.example.demo.model.ShortUrl;
import com.example.demo.model.ShortUrlDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrlDTO, Long> {
    Optional<ShortUrlDTO> findByShortUrl(String shortUrl);
}