package com.medigator.medigator.controller;

import com.medigator.medigator.dto.NewsDTO;
import com.medigator.medigator.service.NaverSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

@Controller
public class HomeController {
    @Autowired
    private NaverSearchService naverSearchService;

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/")
    public String index_notclient() {
        return "index_NotClient";
    }

    @GetMapping("/search/titles")
    @ResponseBody
    public List<NewsDTO> getTitles(@RequestParam String query) {
        return naverSearchService.searchTitles(query);
    }


    @GetMapping("/crawl-image")
    @ResponseBody
    public Map<String, String> crawlImage(@RequestParam String query) {
        String url = query;
        Map<String, String> response = new HashMap<>();
        try {
            // User-Agent 헤더 추가
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();

            Element imageElement = doc.select("meta[property=og:image]").first(); // Open Graph image 태그 찾기
            if (imageElement != null) {
                String imageUrl = imageElement.attr("content");
                response.put("imageUrl", imageUrl);
            } else {
                response.put("imageUrl", ""); // 이미지가 없을 때 빈 문자열 반환
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.put("imageUrl", "Error: " + e.getMessage()); // 에러가 발생했을 때 에러 메시지 반환
        }
        return response;
    }
}
