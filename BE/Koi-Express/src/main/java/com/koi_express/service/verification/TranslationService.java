package com.koi_express.service.verification;

import com.koi_express.exception.TranslationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TranslationService {

    private final RestTemplate restTemplate;
    private final String libreTranslateUrl;

    public TranslationService(RestTemplate restTemplate, @Value("${libretranslate.url:https://libretranslate.com/translate}") String libreTranslateUrl) {
        this.restTemplate = restTemplate;
        this.libreTranslateUrl = libreTranslateUrl;
    }

    public String translateText(String text, String targetLanguage) throws TranslationException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text to translate must not be null or empty.");
        }

        if (targetLanguage == null || targetLanguage.trim().isEmpty()) {
            throw new IllegalArgumentException("Target language must not be null or empty.");
        }

        String url = libreTranslateUrl + "/translate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("q", text);
        requestBody.put("source", "auto");
        requestBody.put("target", targetLanguage);
        requestBody.put("format", "text");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("translatedText")) {
                return (String) responseBody.get("translatedText");
            } else {
                throw new TranslationException("Invalid response from translation service.");
            }
        } catch (Exception e) {
            throw new TranslationException("Error during translation: " + e.getMessage(), e);
        }
    }
}
