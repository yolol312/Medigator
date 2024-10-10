package com.medigator.medigator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medigator.medigator.dto.NewsDTO;
import org.hibernate.mapping.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NaverSearchService {

    private static final String CLIENT_ID = "5HxZgjRQVp94_UAkMoJy";  // 애플리케이션 클라이언트 아이디
    private static final String CLIENT_SECRET = "Y7raCeG6NO";  // 애플리케이션 클라이언트 시크릿

    public String search(String query) {
        String text = UriComponentsBuilder.fromHttpUrl("https://openapi.naver.com/v1/search/news")
                .queryParam("query", query)
                .queryParam("display", 1)
                .toUriString();

        java.util.Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", CLIENT_ID);
        requestHeaders.put("X-Naver-Client-Secret", CLIENT_SECRET);

        return get(text, requestHeaders);
    }

    private static String get(String apiUrl, java.util.Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            requestHeaders.forEach(con::setRequestProperty);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readBody(con.getInputStream());
            } else {
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException("연결 실패: " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("응답 읽기 실패", e);
        }
    }

    public List<NewsDTO> searchTitles(String query) {
        String jsonResponse = search(query); // 기존 search 메서드 사용
        ObjectMapper mapper = new ObjectMapper();
        List<NewsDTO> titles = new ArrayList<>();
        try {
            JsonNode rootNode = mapper.readTree(jsonResponse);
            JsonNode items = rootNode.path("items");
            if (!items.isMissingNode()) { // "items" 필드가 있는지 확인
                for (JsonNode item : items) {
                    NewsDTO news = new NewsDTO();
                    String title = item.path("title").asText();
                    String link = item.path("link").asText();
                    news.setTitleName(title.replaceAll("<[^>]*>", "").replaceAll("&quot;", "")); // HTML 태그 제거);
                    news.setLinkName(link.replaceAll("<[^>]*>", "").replaceAll("&quot;", "")); // HTML 태그 제거
                    titles.add(news); // 제목을 리스트에 추가
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return titles;
    }
}
